package com.example.eximporter.importer.file.manager;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.helper.MappingAttributeHelper;
import com.example.eximporter.importer.service.js.JSParameter;
import com.example.eximporter.importer.service.js.checkin.CheckinService;
import com.example.eximporter.importer.service.js.notification.NotificationService;
import com.example.eximporter.importer.service.parking.ParkedPeoProcessor;
import com.example.eximporter.importer.file.FolderType;
import com.example.eximporter.importer.file.object.FileInfo;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CleanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanService.class);

    @Value("${configuration.storage.day.limit}")
    private Long storageDateLimit;
    private Boolean isSend = false;
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
    private StringBuilder cleaningMessage = new StringBuilder("This files are deleted:" + ParkedPeoProcessor.MSG_NEXTLINE);

    @Autowired
    private FileManager fileManager;

    @Autowired
    private CheckinService checkinService;
    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "${configuration.storage.cron}")
    public void cleanFileStorage() {
        LocalDate today = LocalDate.now();
        LocalDate eailer = today.minusDays(getStorageDateLimit());
        Date threshold = Date.from(eailer.atStartOfDay(ZoneId.systemDefault()).toInstant());
        AgeFileFilter filter = new AgeFileFilter(threshold);
        checkFileStorage(FolderType.SUCCESS, filter);
        checkFileStorage(FolderType.ERROR, filter);
        if (isSend) {
            Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
            parameters.put(JSParameter.MSG_TEMPLATE, MappingAttributeHelper.CLEANING_SERVICE);
            parameters.put(JSParameter.MESSAGE, cleaningMessage.toString());
            notificationService.call(parameters);
        }
        LOGGER.info("End checking folder for cleaning ");
    }

    /**
     * Check folder with some type , and order file with filter
     * @param folderType type folder
     * @param filter day filter
     */
    private void checkFileStorage(FolderType folderType, AgeFileFilter filter) {
        LOGGER.info("Start checking {} folder for cleaning. Day limit: {} ", folderType, storageDateLimit);
        Map<ImportType, List<FileInfo>> filesMap = fileManager.getFilesMap(folderType);
        for (Map.Entry fileType : filesMap.entrySet()) {
            List<FileInfo> fileInfos = (List<FileInfo>) fileType.getValue();
            LOGGER.info("Process folder {}", fileType.getKey());
            fileInfos.forEach(fileInfo -> {
                LOGGER.info("File for analise: {}", fileInfo.getPath());
                File[] filteredFile = FileFilterUtils.filter(filter, fileInfo.getPath().toFile());
                if (filteredFile.length > 0) {
                    LOGGER.info("Delete this file. Date: {}", formatter.format(filteredFile[0].lastModified()));
                    isSend = true;
                    cleaningMessage.append(fileInfo.getPath().toString()).append(ParkedPeoProcessor.MSG_NEXTLINE).append("Date: ").append(formatter.format(filteredFile[0].lastModified())).append(" Status operation: ");
                    deleteFile(fileInfo);
                    callJsScript(fileInfo.getPath().toString());
                } else {
                    LOGGER.info("Skipped file. Date: {}  ", formatter.format(fileInfo.getPath().toFile().lastModified()));
                }
            });


        }

    }

    /**
     * Delete file
     * @param fileInfo
     */
    private void deleteFile(FileInfo fileInfo) {
        String file = fileInfo.getPath().toString();
        try {
            if (fileInfo.getPath().toFile().delete()) {
                LOGGER.info("{}  is deleted!", file);
                cleaningMessage.append(" DELETED").append(ParkedPeoProcessor.MSG_NEXTLINE);
            } else {
                LOGGER.error("{} Delete operation is failed.", file);
                cleaningMessage.append(" OPERATION IS FAILED").append(ParkedPeoProcessor.MSG_NEXTLINE);
            }
        } catch (Exception e) {
            LOGGER.error("{} Delete operation is failed.", file, e);
            cleaningMessage.append(" OPERATION IS FAILED").append(ParkedPeoProcessor.MSG_NEXTLINE);
        }
    }

    /**
     * Call js script for uncheck-in file from system
     * @param file
     */
    private void callJsScript(String file) {
        Map<JSParameter, String> parameters = new EnumMap<>(JSParameter.class);
        parameters.put(JSParameter.FILE_NAME, file);
        checkinService.call(parameters);
    }

    public Long getStorageDateLimit() {
        return storageDateLimit;
    }

    public void setStorageDateLimit(Long storageDateLimit) {
        this.storageDateLimit = storageDateLimit;
    }
}
