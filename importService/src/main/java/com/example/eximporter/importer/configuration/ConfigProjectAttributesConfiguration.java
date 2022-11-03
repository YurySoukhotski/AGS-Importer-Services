package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProjectAttributesConfiguration
{
	@Value("${configuration.attribute.ftp.host}")
	private String ftpHostAttribute;
	@Value("${configuration.attribute.ftp.port}")
	private String ftpPortAttribute;
	@Value("${configuration.attribute.ftp.user.name}")
	private String ftpUserNameAttribute;
	@Value("${configuration.attribute.ftp.user.password}")
	private String ftpUserPasswordAttribute;
	@Value("${configuration.attribute.ftp.project.source.folder}")
	private String projectSourceFolderAttribute;
	@Value("${configuration.attribute.ftp.page.source.folder}")
	private String pageSourceFolderAttribute;
	@Value("${configuration.attribute.ftp.product.source.folder}")
	private String productSourceFolderAttribute;
	@Value("${configuration.attribute.ftp.peo.source.folder}")
	private String peoSourceFolderAttribute;
	@Value("${configuration.attribute.ftp.error.folder}")
	private String ftpErrorFolderAttribute;
	@Value("${configuration.attribute.cron.expression}")
	private String cronExpressionAttribute;
	@Value("${configuration.attribute.max.count.attempts}")
	private String maxCountAttemptsAttribute;
	@Value("${configuration.attribute.project.input.folder}")
	private String projectInputFolderAttribute;
	@Value("${configuration.attribute.project.success.folder}")
	private String projectSuccessFolderAttribute;
	@Value("${configuration.attribute.project.error.folder}")
	private String projectErrorFolderAttribute;
	@Value("${configuration.attribute.project.archive.folder}")
	private String projectArchiveFolderAttribute;
	@Value("${configuration.attribute.project.temp.folder}")
	private String projectTempFolderAttribute;
	@Value("${configuration.attribute.page.input.folder}")
	private String pageInputFolderAttribute;
	@Value("${configuration.attribute.page.success.folder}")
	private String pageSuccessFolderAttribute;
	@Value("${configuration.attribute.page.error.folder}")
	private String pageErrorFolderAttribute;
	@Value("${configuration.attribute.page.archive.folder}")
	private String pageArchiveFolderAttribute;
	@Value("${configuration.attribute.page.temp.folder}")
	private String pageTempFolderAttribute;
	@Value("${configuration.attribute.product.input.folder}")
	private String productInputFolderAttribute;
	@Value("${configuration.attribute.product.success.folder}")
	private String productSuccessFolderAttribute;
	@Value("${configuration.attribute.product.error.folder}")
	private String productErrorFolderAttribute;
	@Value("${configuration.attribute.product.archive.folder}")
	private String productArchiveFolderAttribute;
	@Value("${configuration.attribute.product.temp.folder}")
	private String productTempFolderAttribute;
	@Value("${configuration.attribute.peo.input.folder}")
	private String peoInputFolderAttribute;
	@Value("${configuration.attribute.peo.success.folder}")
	private String peoSuccessFolderAttribute;
	@Value("${configuration.attribute.peo.error.folder}")
	private String peoErrorFolderAttribute;
	@Value("${configuration.attribute.peo.archive.folder}")
	private String peoArchiveFolderAttribute;
	@Value("${configuration.attribute.peo.temp.folder}")
	private String peoTempFolderAttribute;
	@Value("${configuration.attribute.project.file.pattern}")
	private String projectFilePatternAttribute;
	@Value("${configuration.attribute.product.file.pattern}")
	private String productFilePatternAttribute;
	@Value("${configuration.attribute.peo.file.pattern}")
	private String peoFilePatternAttribute;
	@Value("${configuration.attribute.page.file.pattern}")
	private String pageFilePatternAttribute;
	@Value("${configuration.attribute.workflow.js.webservice.url}")
	private String workflowJsWebserviceUrlAttribute;
	@Value("${configuration.attribute.mandator.identity}")
	private String mandatorIdentityAttribute;
	@Value("${configuration.attribute.notification.script.name}")
	private String notificationScriptNameAttribute;
	@Value("${configuration.attribute.checkin.script.name}")
	private String checkinScriptNameAttribute;
	/**
	 * Gets attribute identifier which contains FTP host
	 * @return attribute identifier which contains FTP host
	 */
	public String getFtpHostAttribute()
	{
		return ftpHostAttribute;
	}

	/**
	 * Gets attribute identifier which contains FTP port
	 * @return attribute identifier which contains FTP port
	 */
	public String getFtpPortAttribute()
	{
		return ftpPortAttribute;
	}

	/**
	 * Gets attribute identifier which contains FTP user name
	 * @return attribute identifier which contains FTP user name
	 */
	public String getFtpUserNameAttribute()
	{
		return ftpUserNameAttribute;
	}

	/**
	 * Gets attribute identifier which contains FTP user password
	 * @return attribute identifier which contains FTP user password
	 */
	public String getFtpUserPasswordAttribute()
	{
		return ftpUserPasswordAttribute;
	}

	/**
	 * Gets attribute identifier which contains project source folder
	 * @return attribute identifier which contains project source folder
	 */
	public String getProjectSourceFolderAttribute()
	{
		return projectSourceFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page source folder
	 * @return attribute identifier which contains page source folder
	 */
	public String getPageSourceFolderAttribute()
	{
		return pageSourceFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product source folder
	 * @return attribute identifier which contains product source folder
	 */
	public String getProductSourceFolderAttribute()
	{
		return productSourceFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains peo source folder
	 * @return attribute identifier which contains peo source folder
	 */
	public String getPeoSourceFolderAttribute()
	{
		return peoSourceFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains ftp error folder
	 * @return attribute identifier which contains ftp error folder
	 */
	public String getFtpErrorFolderAttribute()
	{
		return ftpErrorFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains cron expression
	 * @return attribute identifier which contains cron expression
	 */
	public String getCronExpressionAttribute()
	{
		return cronExpressionAttribute;
	}

	/**
	 * Gets attribute identifier which contains max count attempts
	 * @return attribute identifier which contains max count attempts
	 */
	public String getMaxCountAttemptsAttribute()
	{
		return maxCountAttemptsAttribute;
	}

	/**
	 * Gets attribute identifier which contains project input folder
	 * @return attribute identifier which contains project input folder
	 */
	public String getProjectInputFolderAttribute()
	{
		return projectInputFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains project success folder
	 * @return attribute identifier which contains project success folder
	 */
	public String getProjectSuccessFolderAttribute()
	{
		return projectSuccessFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains project error folder
	 * @return attribute identifier which contains project error folder
	 */
	public String getProjectErrorFolderAttribute()
	{
		return projectErrorFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains project archive folder
	 * @return attribute identifier which contains project archive folder
	 */
	public String getProjectArchiveFolderAttribute()
	{
		return projectArchiveFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains project temp folder
	 * @return attribute identifier which contains project temp folder
	 */
	public String getProjectTempFolderAttribute()
	{
		return projectTempFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page input folder
	 * @return attribute identifier which contains page input folder
	 */
	public String getPageInputFolderAttribute()
	{
		return pageInputFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page success folder
	 * @return attribute identifier which contains page success folder
	 */
	public String getPageSuccessFolderAttribute()
	{
		return pageSuccessFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page error folder
	 * @return attribute identifier which contains page error folder
	 */
	public String getPageErrorFolderAttribute()
	{
		return pageErrorFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page archive folder
	 * @return attribute identifier which contains page archive folder
	 */
	public String getPageArchiveFolderAttribute()
	{
		return pageArchiveFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains page temp folder
	 * @return attribute identifier which contains page temp folder
	 */
	public String getPageTempFolderAttribute()
	{
		return pageTempFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product input folder
	 * @return attribute identifier which contains product input folder
	 */
	public String getProductInputFolderAttribute()
	{
		return productInputFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product success folder
	 * @return attribute identifier which contains product success folder
	 */
	public String getProductSuccessFolderAttribute()
	{
		return productSuccessFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product error folder
	 * @return attribute identifier which contains product error folder
	 */
	public String getProductErrorFolderAttribute()
	{
		return productErrorFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product archive folder
	 * @return attribute identifier which contains product archive folder
	 */
	public String getProductArchiveFolderAttribute()
	{
		return productArchiveFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains product temp folder
	 * @return attribute identifier which contains product temp folder
	 */
	public String getProductTempFolderAttribute()
	{
		return productTempFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains PEO input folder
	 * @return attribute identifier which contains PEO input folder
	 */
	public String getPeoInputFolderAttribute()
	{
		return peoInputFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains PEO success folder
	 * @return attribute identifier which contains PEO success folder
	 */
	public String getPeoSuccessFolderAttribute()
	{
		return peoSuccessFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains PEO error folder
	 * @return attribute identifier which contains PEO error folder
	 */
	public String getPeoErrorFolderAttribute()
	{
		return peoErrorFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains PEO archive folder
	 * @return attribute identifier which contains PEO archive folder
	 */
	public String getPeoArchiveFolderAttribute()
	{
		return peoArchiveFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains PEO temp folder
	 * @return attribute identifier which contains PEO temp folder
	 */
	public String getPeoTempFolderAttribute()
	{
		return peoTempFolderAttribute;
	}

	/**
	 * Gets attribute identifier which contains project file pattern
	 * @return attribute identifier which contains project file pattern
	 */
	public String getProjectFilePatternAttribute()
	{
		return projectFilePatternAttribute;
	}

	/**
	 * Gets attribute identifier which contains product file pattern
	 * @return attribute identifier which contains product file pattern
	 */
	public String getProductFilePatternAttribute()
	{
		return productFilePatternAttribute;
	}

	/**
	 * Gets attribute identifier which contains peo file pattern
	 * @return attribute identifier which contains peo file pattern
	 */
	public String getPeoFilePatternAttribute()
	{
		return peoFilePatternAttribute;
	}

	/**
	 * Gets attribute identifier which contains page file pattern
	 * @return attribute identifier which contains page file pattern
	 */
	public String getPageFilePatternAttribute()
	{
		return pageFilePatternAttribute;
	}

	/**
	 * Gets attribute identifier which contains URL for WorkflowJsWebservice
	 * @return attribute identifier which contains URL for WorkflowJsWebservice
	 */
	public String getWorkflowJsWebserviceUrlAttribute()
	{
		return workflowJsWebserviceUrlAttribute;
	}

	/**
	 * Gets attribute identifier which contains mandator identity
	 * @return attribute identifier which contains mandator identity
	 */
	public String getMandatorIdentityAttribute()
	{
		return mandatorIdentityAttribute;
	}

	/**
	 * Gets attribute identifier which contains notification script name
	 * @return attribute identifier which contains NotificationScript name
	 */
	public String getNotificationScriptNameAttribute()
	{
		return notificationScriptNameAttribute;
	}

	/**
	 * Gets attribute identifier which contains checkin script name
	 * @return attribute identifier which contains checkin name
	 */
	public String getCheckinScriptNameAttribute()
	{
		return checkinScriptNameAttribute;
	}
}
