package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobCronConfiguration
{
	@Value("${default.cron.expression}")
	private String cronExpression;
	@Value("${default.max.count.attempts}")
	private Long maxCountAttempts;

	/**
	 * Gets cron expression for jobs
	 * @return cron expression for jobs
	 */
	public String getCronExpression()
	{
		return cronExpression;
	}

	/**
	 * Set cron expression
	 * @param cronExpression
	 *            cron expression
	 */
	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	/**
	 * Gets max count attempts
	 * @return max count attempts
	 */
	public Long getMaxCountAttempts()
	{
		return maxCountAttempts;
	}

	/**
	 * Set max count attempts
	 * @param maxCountAttempts
	 *            max count attempts
	 */
	public void setMaxCountAttempts(Long maxCountAttempts)
	{
		this.maxCountAttempts = maxCountAttempts;
	}
}
