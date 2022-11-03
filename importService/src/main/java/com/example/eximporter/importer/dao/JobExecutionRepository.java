package com.example.eximporter.importer.dao;

import com.example.eximporter.importer.model.extended.JobExecutionParameter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface JobExecutionRepository extends CrudRepository<JobExecutionParameter, Long>
{
}
