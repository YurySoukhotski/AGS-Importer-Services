package com.example.eximporter.importer.service.job;

import com.example.eximporter.importer.dao.JobExecutionRepository;
import com.example.eximporter.importer.model.extended.JobExecutionParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to work with Parked Page
 */
@Service
public class JobExecutionService {
    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    public void saveJobExecution(JobExecutionParameter jobExecutionParameter) {
        jobExecutionRepository.save(jobExecutionParameter);
    }

    void deletePage(JobExecutionParameter jobExecutionParameter) {
        jobExecutionRepository.delete(jobExecutionParameter);
    }

    Iterable<JobExecutionParameter> getJobExecutions() {
        return jobExecutionRepository.findAll();
    }
}
