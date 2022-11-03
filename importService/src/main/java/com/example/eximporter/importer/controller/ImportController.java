package com.example.eximporter.importer.controller;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.file.manager.FileManager;
import com.example.eximporter.importer.file.object.FileInfo;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.model.extended.JobExecutionParameter;
import com.example.eximporter.importer.service.ftp.FtpService;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.job.JobExecutionProcessor;
import com.example.eximporter.importer.service.js.JSParameter;
import com.example.eximporter.importer.service.js.notification.NotificationService;
import com.example.eximporter.importer.service.parking.ParkedPeoProcessor;
import com.example.eximporter.importer.file.FolderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.eximporter.importer.controller.ImportController.TaskStatus.*;

/**
 * Service to manage import process
 */
@Service
public class ImportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportController.class);
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importProductToOmn;
    @Autowired
    private Job importProjectToOmn;
    @Autowired
    private Job importPageToOmn;
    @Autowired
    private Job importPeoToOmn;
    @Autowired
    private FtpService ftpService;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JobExecutionProcessor jobExecutionProcessor;

    Map<String, TaskStatus> executedTasks = new HashMap<>();
    volatile EnumMap<ImportType, String> lastFailedJobMessages = new EnumMap<>(ImportType.class);


    /**
     * Start in cron scan folder to new files and run jobs with parameters like name this file
     */
    public void startImport() {
        try {
            LOGGER.debug("------------- Start checking new files -------------");
            ftpService.copyFilesFromFtp();
            fileManager.unzipFiles();
            Map<ImportType, List<FileInfo>> filesMap = fileManager.getFilesMap(FolderType.INPUT);
            if (filesMap.get(ImportType.PRODUCT) != null) {
                filesMap.get(ImportType.PRODUCT).stream().forEach(this::startJob);
            } else if (filesMap.get(ImportType.PROJECT) != null) {
                filesMap.get(ImportType.PROJECT).stream().forEach(this::startJob);
            } else if (filesMap.get(ImportType.PAGE) != null) {
                filesMap.get(ImportType.PAGE).stream().forEach(this::startJob);
            } else if (filesMap.get(ImportType.PEO) != null) {
                filesMap.get(ImportType.PEO).stream().forEach(this::startJob);
            } else {
                LOGGER.debug("No files found. Jobs not started");
            }
        } catch (Exception e) {
            LOGGER.error("Exception while running Job", e);
        }
    }

    /**
     * Starts job
     *
     * @param fileInfo {@link FileInfo} object
     */
    private void startJob(FileInfo fileInfo) {
        LOGGER.info("Start job for file: {}", fileInfo.getPath());
        Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
        parameters.put(JSParameter.FILE_PATH, fileInfo.getPath().toString());
        if (!executedTasks.containsValue(RUN)) {
            filterJob(fileInfo, parameters);
        }
    }

    /**
     * Filter job for begin execution
     *
     * @param fileInfo   received file for analise
     * @param parameters service for send message
     */
    private void filterJob(FileInfo fileInfo, Map<JSParameter, String> parameters) {
        JobExecutionParameter jobExecutionByType = jobExecutionProcessor.getJobExecutionByType(fileInfo.getImportType());
        LOGGER.info("Last processed file for type: {}  has sequence number: {}  and status: {}. Count attempts: {}  ", jobExecutionByType.getImportType(), jobExecutionByType.getIndex(), jobExecutionByType.getStatus(), jobExecutionByType.getAttempt());
        try {
            if (isJobWillStart(fileInfo, jobExecutionByType)) {
                executedTasks.put(fileInfo.getPath().toString(), RUN);
                jobExecutionProcessor.saveModifiedParameter(jobExecutionByType, fileInfo, DONE.name());
                launchJob(fileInfo);
            }
            if (isJobNumberLower(fileInfo, jobExecutionByType)) {
                fileManager.moveFile(fileInfo.getPath().toString(), FolderType.ERROR, true);
                executedTasks.put(fileInfo.getPath().toString(), FAIL);
                parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.WRONG_SEQ_NUMBER);
                parameters.put(JSParameter.MESSAGE, "Job will not start because file has sequence number: " + fileInfo.getOrder() + " is lower or equal to the last processed number for type: "
                        + fileInfo.getImportType().toString() + " and number: " + jobExecutionByType.getIndex() + ParkedPeoProcessor.MSG_NEXTLINE + "File can be found in Error folder: "
                        + fileManager.getFolder(fileInfo.getImportType(), FolderType.ERROR) + "/" + fileInfo.getPath().toString().substring(fileInfo.getPath().toString().lastIndexOf(ProjectApiService.DELIMITER) + 1));
                parameters.put(JSParameter.FILE_NAME, fileInfo.getPath().toString().substring(fileInfo.getPath().toString().lastIndexOf(ProjectApiService.DELIMITER) + 1));
                notificationService.call(parameters);
                LOGGER.error("Job will not start because file has sequence number: {} is lower or equal to the last processed ", fileInfo.getOrder());
            }
            if (isLastJobFailed(fileInfo, jobExecutionByType)) {
                executedTasks.put(fileInfo.getPath().toString(), FAIL);
                if (lastFailedJobMessages.get(fileInfo.getImportType())==null) {
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.WRONG_SEQ_NUMBER);
                    parameters.put(JSParameter.MESSAGE, "Job will not start because last processed job: " + jobExecutionByType.getFileName().substring(jobExecutionByType.getFileName().lastIndexOf(ProjectApiService.DELIMITER) + 1) + " is failed." + ParkedPeoProcessor.MSG_NEXTLINE +
                            "Fix problem and restart failed job again");
                    parameters.put(JSParameter.FILE_NAME, fileInfo.getPath().toString().substring(fileInfo.getPath().toString().lastIndexOf(ProjectApiService.DELIMITER) + 1));
                    notificationService.call(parameters);
                    lastFailedJobMessages.put(fileInfo.getImportType(), fileInfo.getPath().toString());
                    LOGGER.error("Job not starter because last processed file is failed. Send message", jobExecutionByType.getAttempt());
                } else
                    LOGGER.error("Job not starter because last processed file is failed. Message already sent", jobExecutionByType.getAttempt());
            }
            if (isRestartFailed(fileInfo, jobExecutionByType)) {
                executedTasks.put(fileInfo.getPath().toString(), RUN);
                jobExecutionProcessor.saveModifiedParameter(jobExecutionByType, fileInfo, DONE.name());
                launchJob(fileInfo);
                lastFailedJobMessages.remove(fileInfo.getImportType());
            }
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException e) {
            LOGGER.error("Skip file {}", fileInfo.getPath().toString(), e);
            fileManager.moveFile(fileInfo.getPath().toString(), FolderType.ERROR, true);
            executedTasks.put(fileInfo.getPath().toString(), DONE);
            parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.JOB_ALREADY_COMPLETED);
            notificationService.call(parameters);
            jobExecutionProcessor.saveModifiedParameter(jobExecutionByType, fileInfo, FAIL.name());
        } catch (JobParametersInvalidException | JobRestartException e) {
            LOGGER.error("Can't run job for file {}", fileInfo.getPath().toString(), e);
            fileManager.moveFile(fileInfo.getPath().toString(), FolderType.ERROR, true);
            executedTasks.put(fileInfo.getPath().toString(), FAIL);
            parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.JOB_ALREADY_COMPLETED);
            notificationService.call(parameters);
            jobExecutionProcessor.saveModifiedParameter(jobExecutionByType, fileInfo, FAIL.name());
        }

    }

    /**
     * Run separate job
     *
     * @param fileInfo {@link FileInfo} object
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    private boolean launchJob(FileInfo fileInfo)
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException {
        Job currentJob;
        Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
        parameters.put(JSParameter.FILE_PATH, fileInfo.getPath().toString());
        parameters.put(JSParameter.FILE_NAME, fileInfo.getPath().toString().substring(fileInfo.getPath().toString().lastIndexOf(
                ProjectApiService.DELIMITER) + 1));
        switch (fileInfo.getImportType()) {
            case ImportType.PRODUCT:
                LOGGER.info(" --------- Start Import Product Job ---------");
                parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.ARTICLE_IMPORT_ARRIVED);
                currentJob = importProductToOmn;
                break;
            case ImportType.PROJECT:
                LOGGER.info(" --------- Start Import Project Job ---------");
                parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.CATALOG_IMPORT_ARRIVED);
                currentJob = importProjectToOmn;
                break;
            case ImportType.PAGE:
                LOGGER.info(" --------- Start Import Page Job ---------");
                parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.PAGE_IMPORT_ARRIVED);
                currentJob = importPageToOmn;
                break;
            case ImportType.PEO:
                LOGGER.info(" --------- Start Import Peo Job ---------");
                parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.PEO_IMPORT_ARRIVED);
                currentJob = importPeoToOmn;
                break;
            case ImportType.UNDEFINED:
            default:
                LOGGER.error("Can't determine the type of import for {} file . The file name does not match the entered masks.",
                        fileInfo.getPath());
                return false;
        }
        notificationService.call(parameters);
        jobLauncher.run(currentJob,
                new JobParametersBuilder().addString(StepListener.FILE_PATH_JOB_PARAMETER, fileInfo.getPath().toString())
                        .toJobParameters());
        return true;
    }

    /**
     * Normal case when job will be start
     *
     * @param fileInfo
     * @param jobExecutionByType
     * @return
     */
    private boolean isJobWillStart(FileInfo fileInfo, JobExecutionParameter jobExecutionByType) {
        return (fileInfo.getOrder() > jobExecutionByType.getIndex() && jobExecutionByType.getStatus().equals(DONE.name())) ||
                (fileInfo.getOrder() > jobExecutionByType.getIndex() && jobExecutionByType.getAttempt() >= 3);
    }


    /**
     * Return TRUE when last job is failed
     *
     * @param fileInfo
     * @param jobExecutionByType
     * @return
     */
    private boolean isLastJobFailed(FileInfo fileInfo, JobExecutionParameter jobExecutionByType) {
        return fileInfo.getOrder() > jobExecutionByType.getIndex() && jobExecutionByType.getStatus().equals(FAIL.name());
    }

    /**
     * Return true when try to restart failed job up to 3 attempts
     *
     * @param fileInfo
     * @param jobExecutionByType
     * @return
     */
    private Boolean isRestartFailed(FileInfo fileInfo, JobExecutionParameter jobExecutionByType) {
        return fileInfo.getOrder().equals(jobExecutionByType.getIndex()) && jobExecutionByType.getStatus().equals(FAIL.name()) && jobExecutionByType.getAttempt() < 3;
    }


    private boolean isJobNumberLower(FileInfo fileInfo, JobExecutionParameter jobExecutionByType) {
        return fileInfo.getOrder() <= jobExecutionByType.getIndex() && jobExecutionByType.getStatus().equals(DONE.name()) && executedTasks.get(fileInfo.getPath().toString()) == null;
    }

    public enum TaskStatus {
        RUN, FAIL, DONE
    }


}
