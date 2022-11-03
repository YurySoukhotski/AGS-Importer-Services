package com.example.eximporter.importer.controller;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.file.manager.FileManager;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.helper.MessageBuilderHelper;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.job.JobExecutionProcessor;
import com.example.eximporter.importer.service.js.JSParameter;
import com.example.eximporter.importer.service.js.notification.NotificationService;
import com.example.eximporter.importer.file.FolderType;
import com.example.eximporter.importer.service.parking.ParkedPageService;
import com.example.eximporter.importer.service.parking.ParkedPeoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.example.eximporter.importer.controller.ImportController.TaskStatus.DONE;
import static com.example.eximporter.importer.controller.ImportController.TaskStatus.FAIL;


/**
 * Component check the status of the job
 */
@Component
public class StepListener implements StepExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepListener.class);
    public static final String FILE_PATH_JOB_PARAMETER = "filePath";
    public static final String PARTIALLY_PROCESSED_MESSAGE = "partiallyProcessedMessage";

    @Autowired
    private FileManager fileManager;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ParkedPageService parkedPageService;
    @Autowired
    private ParkedPeoService parkedPeoService;
    @Autowired
    private ImportController importController;
    @Autowired
    private JobExecutionProcessor jobExecutionProcessor;

    /**
     * Check parked pages, and try to process it again
     *
     * @param stepExecution current step context
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        parkedPageService.checkParkedPages(stepExecution);
        parkedPeoService.checkParkedPeos(stepExecution);
    }

    /**
     * After step execution check status of job and move file to success or error folders
     *
     * @param stepExecution current step context
     *                      step execution
     * @return execution status
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        LOGGER.info("  ----  Checking status of job ---------- ");
        String filePath = stepExecution.getJobParameters().getString(FILE_PATH_JOB_PARAMETER);
        Boolean isPartially = false;
        String partiallyMessage = String.valueOf(stepExecution.getJobExecution().getExecutionContext().get(PARTIALLY_PROCESSED_MESSAGE));
        if (!"null".equalsIgnoreCase(partiallyMessage)) {
            isPartially = Boolean.TRUE;
        }
        Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED) && !isPartially) {
            LOGGER.info("Job is finished. Move file to success folder. Status of job : {} ",
                    stepExecution.getExitStatus().getExitCode());
            fileManager.moveFile(filePath, FolderType.SUCCESS, true);
            switch (stepExecution.getStepName()) {
                case "importProduct":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.FINISH_IMPORT_ARTICLE);
                    break;
                case "importProject":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.FINISH_IMPORT_CATALOG);
                    break;
                case "importPage":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.FINISH_IMPORT_PAGE);
                    break;
                case "importPeo":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.FINISH_IMPORT_PEO);
                    break;
                default:
                    parameters.put(JSParameter.MSG_TEMPLATE, "");
                    break;
            }
            parameters.put(JSParameter.FILE_PATH, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            parameters.put(JSParameter.FILE_NAME, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            parameters.put(JSParameter.MESSAGE, MessageBuilderHelper.getMessageInfo(stepExecution));
            notificationService.call(parameters);
            sendParkedServiceMessage(stepExecution);
            importController.executedTasks.put(filePath, DONE);
        } else if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED) && isPartially) {
            LOGGER.info("Job is partially finished. Move file to success folder");
            fileManager.moveFile(filePath, FolderType.SUCCESS, true);
            parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.PROBLEM_WITH_PEO);
            parameters.put(JSParameter.FILE_PATH, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            parameters.put(JSParameter.FILE_NAME, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            parameters.put(JSParameter.MESSAGE, partiallyMessage);
            notificationService.call(parameters);
            sendParkedServiceMessage(stepExecution);
            importController.executedTasks.put(filePath, DONE);
        } else {
            LOGGER.error("Job is failed. Moving file to error folder Status of job : {} ",
                    stepExecution.getExitStatus().getExitCode());
            fileManager.moveFile(filePath, FolderType.ERROR, true);
            switch (stepExecution.getStepName()) {
                case "importProduct":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.ERROR_EXEC_ARTICLE);
                    jobExecutionProcessor.updateStatusParameter(jobExecutionProcessor.getJobExecutionByType(ImportType.PRODUCT), FAIL.name());
                    break;
                case "importProject":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.ERROR_EXEC_CATALOG);
                    jobExecutionProcessor.updateStatusParameter(jobExecutionProcessor.getJobExecutionByType(ImportType.PROJECT), FAIL.name());
                    break;
                case "importPage":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.ERROR_EXEC_PAGE);
                    jobExecutionProcessor.updateStatusParameter(jobExecutionProcessor.getJobExecutionByType(ImportType.PAGE), FAIL.name());
                    break;
                case "importPeo":
                    parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.ERROR_EXEC_PEO);
                    jobExecutionProcessor.updateStatusParameter(jobExecutionProcessor.getJobExecutionByType(ImportType.PEO), FAIL.name());
                    break;
                default:
                    parameters.put(JSParameter.MSG_TEMPLATE, "");
                    break;
            }
            parameters.put(JSParameter.FILE_PATH, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            parameters.put(JSParameter.MESSAGE, MessageBuilderHelper.getMessageInfo(stepExecution));
            parameters.put(JSParameter.FILE_NAME, filePath.substring(filePath.lastIndexOf(ProjectApiService.DELIMITER) + 1));
            notificationService.call(parameters);
            sendParkedServiceMessage(stepExecution);
            importController.executedTasks.put(filePath, FAIL);
        }
        return null;
    }

    private void sendParkedServiceMessage(StepExecution stepExecution) {
        Map<String, String> messageForParkedService = MessageBuilderHelper.getMessageForParkedService(stepExecution);
        Map<JSParameter, String> parameters;
        for (Map.Entry entry : messageForParkedService.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            parameters = new EnumMap<>(JSParameter.class);
            parameters.put(JSParameter.MSG_TEMPLATE, key);
            parameters.put(JSParameter.MESSAGE, value);
            notificationService.call(parameters);
        }
    }
}
