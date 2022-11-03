package com.example.eximporter.importer.controller;

import com.example.eximporter.importer.configuration.JobCronConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Scheduling import configuration
 */
@Configuration
public class ImportSchedulerConfig
{
	@Autowired
	private JobCronConfiguration jobCronConfiguration;

	@Bean
	public MethodInvokingJobDetailFactoryBean importJob()
	{
		MethodInvokingJobDetailFactoryBean importJob = new MethodInvokingJobDetailFactoryBean();
		importJob.setTargetBeanName("importController");
		importJob.setTargetMethod("startImport");
		importJob.setConcurrent(false);
		return importJob;
	}

	@Bean
	public CronTriggerFactoryBean importCronTrigger()
	{
		CronTriggerFactoryBean importCronTrigger = new CronTriggerFactoryBean();
		importCronTrigger.setJobDetail(importJob().getObject());
		importCronTrigger.setCronExpression(jobCronConfiguration.getCronExpression());
		return importCronTrigger;
	}

	@Bean
	public SchedulerFactoryBean importSchedulerFactoryBean()
	{
		SchedulerFactoryBean importSchedulerFactoryBean = new SchedulerFactoryBean();
		importSchedulerFactoryBean.setTriggers(importCronTrigger().getObject());
		importSchedulerFactoryBean.setJobDetails(importJob().getObject());
		return importSchedulerFactoryBean;
	}
}
