package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration
{
	@Value("${configuration.project.id}")
	private Long configurationProjectId;
	@Value("${configuration.scheduler.cron.expression}")
	private String schedulerCronExpression;
	@Value("${configuration.production.id}")
	private Long productionId;

	@Value("${import.peo.templateId}")
	private Long peoTemplateId;

	@Value("${import.peo.template.name}")
	private String peoTemplateName;

	/**
	 * Gets configuration project identity
	 * @return configuration project identity
	 */
	public Long getConfigurationProjectId()
	{
		return configurationProjectId;
	}

	/**
	 * Gets scheduler cron expression
	 * @return scheduler cron expression
	 */
	public String getSchedulerCronExpression()
	{
		return schedulerCronExpression;
	}

	/**
	 * Gets production identity
	 * @return production identity
	 */
	public Long getProductionId()
	{
		return productionId;
	}

	/**
	 * Gets peo template id
	 * @return if of template
	 */
	public Long getPeoTemplateId()
	{
		return peoTemplateId;
	}

	/**
	 * Gets name of template id
	 * @return name of template
	 */
	public String getPeoTemplateName()
	{
		return peoTemplateName;
	}
}
