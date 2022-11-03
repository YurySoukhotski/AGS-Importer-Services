package com.example.eximporter.importer.service.job;

import com.example.eximporter.importer.file.ImportType;
import com.example.eximporter.importer.file.object.FileInfo;
import com.example.eximporter.importer.model.extended.JobExecutionParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.eximporter.importer.controller.ImportController.TaskStatus.DONE;

@Service
public class JobExecutionProcessor {

    @Autowired
    private JobExecutionService jobExecutionService;


    public JobExecutionParameter getJobExecutionByType(ImportType importType) {

        Iterable<JobExecutionParameter> jobExecutions = jobExecutionService.getJobExecutions();
        for (JobExecutionParameter jobExecutionParameter : jobExecutions) {
            if (jobExecutionParameter.getImportType().equals(importType.toString())) {
                return jobExecutionParameter;
            }
        }

        JobExecutionParameter jobExecutionParameter = new JobExecutionParameter();
        jobExecutionParameter.setImportType(importType.toString());
        jobExecutionParameter.setIndex(0L);
        jobExecutionParameter.setAttempt(0L);
        jobExecutionParameter.setStatus(DONE.toString());
        return jobExecutionParameter;
    }

    public void saveModifiedParameter(JobExecutionParameter jobExecutionByType, FileInfo fileInfo, String status){
        Long counter =1L;
        if (jobExecutionByType.getIndex().equals(fileInfo.getOrder())){
            counter=jobExecutionByType.getAttempt()+1;
        }
        jobExecutionByType.setAttempt(counter);
        jobExecutionByType.setFileName(fileInfo.getPath().toString());
        jobExecutionByType.setIndex(fileInfo.getOrder());
        jobExecutionByType.setStatus(status);
        jobExecutionService.saveJobExecution(jobExecutionByType);
    }

    public void updateStatusParameter(JobExecutionParameter jobExecutionByType, String status){
        jobExecutionByType.setStatus(status);
        jobExecutionService.saveJobExecution(jobExecutionByType);
    }

}
