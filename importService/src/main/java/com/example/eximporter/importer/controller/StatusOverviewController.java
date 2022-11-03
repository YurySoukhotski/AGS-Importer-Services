package com.example.eximporter.importer.controller;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.file.manager.FileManager;
import com.example.eximporter.importer.file.object.FileInfo;
import com.example.eximporter.importer.service.job.JobExecutionProcessor;
import com.example.eximporter.importer.file.FolderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.example.eximporter.importer.controller.ImportController.TaskStatus.RUN;

@RestController
public class StatusOverviewController {

    private static final String TABLE_DELIMITTER = "<table width='600' height='1' bgcolor='#D6E4FF'><tr><td> </td></tr></table><br><br>";
    private static final String TD_STATUS_START = "<table width='800'><tr><td width='200'><font face='Verdana'><h1><b>Current ExImporter Status</b><h1></font></td><td width='600'><font face='Verdana'><b>";
    private static final String TD_STATUS_END = "</b></font></td></tr>";
    private static final String TR_PROCESSED = "</b></font></td></tr><tr><td width='200'><font face='Verdana'><h1><b>Processed file:</b><h1></font></td><td width='600'><font face='Verdana'><b>";

    private static final String TABLE_END = "</table><br><br>";
    private static final String TABLE_PENDING = "<table width='800'><tr><td width='200'><font face='Verdana'><h1><b>Pending files</b><h1></font></td></tr></table><br><br>";
    private static final String TABLE_LAST_STATUS = "<table width='800'><tr><td width='200'><font face='Verdana'><h1><b>Last processed files status:</b><h1></font></td></tr></table><br><br>";
    private static final String TABLE_HEADER = "<table width='1400' border='2'><tr><td width='350'><font face='Verdana'><b>Product files</b></font></td><td width='350'><font face='Verdana'><b>Project files</b></font></td>" +
            "<td width='350'><font face='Verdana'><b>Page files</b></font></td><td width='350'><font face='Verdana'><b>Peo files</b></font></td></tr></table>";
    private static final String TABLE_LAST_STATUS_HEADER = "<table width='800' border='2'><tr><td width='100'><font face='Verdana'><b>Type of job</b></font></td><td width='600'><font face='Verdana'><b>File name</b></font></td>" +
            "<td width='100'><font face='Verdana'><b>Status</b></font></td></tr></table><br>";
    private static final String TABLE_LAST_STATUS_BODY_START = "<table width='800'><tr><td width='100'><font face='Verdana'><b>";
    private static final String TABLE_LAST_STATUS_BODY_TD = "</b></font></td><td width='600'><font face='Verdana'><b>";
    private static final String TABLE_LAST_STATUS_BODY_TD_SMALL = "</b></font></td><td width='100'><font face='Verdana'><b>";
    private static final String TABLE_LAST_STATUS_BODY_END = "</b></font></td></tr></table>";

    private static final String TABLE_PENDING_START = "<br><table width='1400'>";
    private static final String TABLE_PENDING_END = "</table><br>";
    private static final String TABLE_PENDING_TR_START = "<tr>";
    private static final String TABLE_PENDING_TR_END = "</font></td></tr>";
    private static final String TABLE_PENDING_TD_START = "<td width='350'><font face='Verdana'>";
    private static final String TABLE_PENDING_TD_END = "</font></td>";

    @Autowired
    private FileManager fileManager;

    @Autowired
    private ImportController importController;
    @Autowired
    private JobExecutionProcessor jobExecutionProcessor;

    @RequestMapping("/getstatus")
    public ResponseEntity startJob() {
        StringBuilder answer = new StringBuilder();
        answer.append(TABLE_DELIMITTER);

        if (importController.executedTasks.containsValue(RUN)) {
            String runKey = getKeyByValue(importController.executedTasks, "RUN");
            answer.append(TD_STATUS_START + "Running" + TR_PROCESSED + runKey.substring(runKey.lastIndexOf('/') + 1) + TD_STATUS_END + TABLE_END);
        } else {
            answer.append(TD_STATUS_START + "Idle" + TD_STATUS_END + TABLE_END);
        }
        answer.append(TABLE_DELIMITTER);
        answer.append(TABLE_PENDING);
        answer.append(TABLE_HEADER);
        buildPendingFilesInfoMessage(answer);
        answer.append(TABLE_DELIMITTER);
        answer.append(TABLE_LAST_STATUS);
        answer.append(TABLE_LAST_STATUS_HEADER);
        buildLastFileInfoMessage(ImportType.PRODUCT, answer);
        buildLastFileInfoMessage(ImportType.PROJECT, answer);
        buildLastFileInfoMessage(ImportType.PAGE, answer);
        buildLastFileInfoMessage(ImportType.PEO, answer);
        return new ResponseEntity(answer.toString(), HttpStatus.OK);
    }

    private String getKeyByValue(Map<String, ImportController.TaskStatus> map, String value) {
        for (Map.Entry<String, ImportController.TaskStatus> entry : map.entrySet()) {
            entry.getKey();
            entry.getValue();
            if (String.valueOf(entry.getValue()).equals(value)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void buildLastFileInfoMessage(ImportType type, StringBuilder stringBuilder) {
        stringBuilder.append(TABLE_LAST_STATUS_BODY_START).append(type).append(TABLE_LAST_STATUS_BODY_TD)
                .append(jobExecutionProcessor.getJobExecutionByType(type).getFileName().split("Import/")[1]).append(TABLE_LAST_STATUS_BODY_TD_SMALL)
                .append(jobExecutionProcessor.getJobExecutionByType(type).getStatus()).append(TABLE_LAST_STATUS_BODY_END);
    }

    private void buildPendingFilesInfoMessage(StringBuilder stringBuilder) {
        Map<ImportType, List<FileInfo>> filesMap = fileManager.getFilesMap(FolderType.INPUT);
        int countRow = 0;
        for (Map.Entry<ImportType, List<FileInfo>> entryType : filesMap.entrySet()) {

            if (entryType.getValue() != null && entryType.getValue().size() > countRow)
                countRow = entryType.getValue().size();
        }

        if (countRow > 0) {
            stringBuilder.append(TABLE_PENDING_START);
            for (int i = 0; i < countRow; i++) {
                stringBuilder.append(TABLE_PENDING_TR_START).append(
                        TABLE_PENDING_TD_START).append(getFileByType(filesMap.get(ImportType.PRODUCT), i))
                        .append(TABLE_PENDING_TD_END).append(TABLE_PENDING_TD_START)
                        .append(getFileByType(filesMap.get(ImportType.PROJECT), i))
                        .append(TABLE_PENDING_TD_END).append(TABLE_PENDING_TD_START).append(getFileByType(filesMap.get(ImportType.PAGE), i))
                        .append(TABLE_PENDING_TD_END).append(TABLE_PENDING_TD_START).append(getFileByType(filesMap.get(ImportType.PEO), i))
                        .append(TABLE_PENDING_TR_END);
            }
            stringBuilder.append(TABLE_PENDING_END);
        }
    }

    private String getFileByType(List<FileInfo> fileInfos, int index) {

        if (fileInfos != null && fileInfos.size() > index) {
            String file = fileInfos.get(index).getPath().toString();
            return file.substring(file.lastIndexOf('/') + 1);
        } else
            return "";
    }

}

