package com.example.eximporter.importer.helper;

import java.text.ParseException;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.example.eximporter.importer.configuration.CommonConfiguration;
import com.example.eximporter.importer.configuration.FtpConfiguration;
import com.example.eximporter.importer.configuration.folder.impl.PageFolderConfiguration;
import com.example.eximporter.importer.configuration.folder.impl.PeoFolderConfiguration;
import com.example.eximporter.importer.configuration.folder.impl.ProductFolderConfiguration;
import com.example.eximporter.importer.configuration.folder.impl.ProjectFolderConfiguration;
import com.example.eximporter.importer.service.http.AuthService;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.eximporter.importer.configuration.ConfigProjectAttributesConfiguration;
import com.example.eximporter.importer.configuration.FilePatternsConfiguration;
import com.example.eximporter.importer.configuration.JobCronConfiguration;
import com.example.eximporter.importer.configuration.ScriptConfiguration;
import com.example.eximporter.importer.model.api.AttributeValues;
import com.example.eximporter.importer.model.api.AttributesValues;
import com.example.eximporter.importer.model.api.Project;
import com.example.eximporter.importer.service.http.ProjectApiService;
import com.example.eximporter.importer.service.http.RestException;

/**
 * Helps to read configuration project
 */
@Service
public class ImportPropertyHelper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportPropertyHelper.class);
	/**
	 * Name of trigger to start import
	 */
	private static final String IMPORT_CRON_TRIGGER = "importCronTrigger";
	@Autowired
	private ProjectApiService projectApiService;
	@Autowired
	private AuthService authService;
	@Autowired
	private SchedulerFactoryBean importSchedulerFactoryBean;
	@Autowired
	private CommonConfiguration commonConfiguration;
	@Autowired
	private ConfigProjectAttributesConfiguration attributesConfiguration;
	@Autowired
	private FtpConfiguration ftpConfiguration;
	@Autowired
	private JobCronConfiguration jobCronConfiguration;
	@Autowired
	private ProjectFolderConfiguration projectFolderConfiguration;
	@Autowired
	private PageFolderConfiguration pageFolderConfiguration;
	@Autowired
	private ProductFolderConfiguration productFolderConfiguration;
	@Autowired
	private PeoFolderConfiguration peoFolderConfiguration;
	@Autowired
	private FilePatternsConfiguration filePatternsConfiguration;
	@Autowired
	private ScriptConfiguration scriptConfiguration;

	/**
	 * Initialization of cron expression
	 */
	@PostConstruct
	public void initCronExpression() throws RestException {
		LOGGER.info("Getting token before start");
		if (authService.getTokenString() == null) {
			getLoginToken();
		}
		LOGGER.info("Initialize cron expression");
		updateImportSchedulerCron();
	}

	/**
	 * Method updates cron expression to schedule task from configuration project
	 */
	@Scheduled(cron = "${configuration.scheduler.cron.expression}")
	public void updateImportSchedulerCron() throws RestException {
		LOGGER.info("Checking token while working");
		if (authService.getTokenString() == null) {
			getLoginToken();
		} else {
			if (!authService.validateToken()){
				getLoginToken();
			}
		}
		updateConfiguration();
		rescheduleImport();
	}

	private void updateConfiguration()
	{
		try
		{
			Project configProject = getConfigurationProject();
			updateFTPConfiguration(configProject.getAttributes());
			updateJobCronConfiguration(configProject.getAttributes());
			updateFolderConfiguration(configProject.getAttributes());
			updateFilePatternConfiguration(configProject.getAttributes());
			updateScriptConfiguration(configProject.getAttributes());
		}
		catch (RestException e)
		{
			LOGGER.error("Can't access to config project. Cron expression won't be set", e);
		}
	}

	private void updateFTPConfiguration(AttributesValues values)
	{
		ftpConfiguration.setHost(getValue(attributesConfiguration.getFtpHostAttribute(), values));
		ftpConfiguration.setPort(getValue(attributesConfiguration.getFtpPortAttribute(), values));
		ftpConfiguration.setUserName(getValue(attributesConfiguration.getFtpUserNameAttribute(), values));
		ftpConfiguration.setUserPassword(getValue(attributesConfiguration.getFtpUserPasswordAttribute(), values));
		ftpConfiguration.setProjectSourceFolder(getValue(attributesConfiguration.getProjectSourceFolderAttribute(), values));
		ftpConfiguration.setPageSourceFolder(getValue(attributesConfiguration.getPageSourceFolderAttribute(), values));
		ftpConfiguration.setProductSourceFolder(getValue(attributesConfiguration.getProductSourceFolderAttribute(), values));
		ftpConfiguration.setPeoSourceFolder(getValue(attributesConfiguration.getPeoSourceFolderAttribute(), values));
		ftpConfiguration.setErrorFolder(getValue(attributesConfiguration.getFtpErrorFolderAttribute(), values));
	}

	private void updateJobCronConfiguration(AttributesValues values)
	{
		jobCronConfiguration.setCronExpression(getValue(attributesConfiguration.getCronExpressionAttribute(), values));
		jobCronConfiguration.setMaxCountAttempts(
			NumberUtils.createDouble(getValue(attributesConfiguration.getMaxCountAttemptsAttribute(), values)).longValue());
	}

	private void updateFolderConfiguration(AttributesValues values)
	{
		updateProjectFolderConfiguration(values);
		updatePageFolderConfiguration(values);
		updateProductFolderConfiguration(values);
		updatePeoFolderConfiguration(values);
	}

	private void updateProjectFolderConfiguration(AttributesValues values)
	{
		projectFolderConfiguration.setInputFolder(getValue(attributesConfiguration.getProjectInputFolderAttribute(), values));
		projectFolderConfiguration.setSuccessFolder(getValue(attributesConfiguration.getProjectSuccessFolderAttribute(), values));
		projectFolderConfiguration.setErrorFolder(getValue(attributesConfiguration.getProjectErrorFolderAttribute(), values));
		projectFolderConfiguration.setArchiveFolder(getValue(attributesConfiguration.getProjectArchiveFolderAttribute(), values));
		projectFolderConfiguration.setTempFolder(getValue(attributesConfiguration.getProjectTempFolderAttribute(), values));
	}

	private void updatePageFolderConfiguration(AttributesValues values)
	{
		pageFolderConfiguration.setInputFolder(getValue(attributesConfiguration.getPageInputFolderAttribute(), values));
		pageFolderConfiguration.setSuccessFolder(getValue(attributesConfiguration.getPageSuccessFolderAttribute(), values));
		pageFolderConfiguration.setErrorFolder(getValue(attributesConfiguration.getPageErrorFolderAttribute(), values));
		pageFolderConfiguration.setArchiveFolder(getValue(attributesConfiguration.getPageArchiveFolderAttribute(), values));
		pageFolderConfiguration.setTempFolder(getValue(attributesConfiguration.getPageTempFolderAttribute(), values));
	}

	private void updateProductFolderConfiguration(AttributesValues values)
	{
		productFolderConfiguration.setInputFolder(getValue(attributesConfiguration.getProductInputFolderAttribute(), values));
		productFolderConfiguration.setSuccessFolder(getValue(attributesConfiguration.getProductSuccessFolderAttribute(), values));
		productFolderConfiguration.setErrorFolder(getValue(attributesConfiguration.getProductErrorFolderAttribute(), values));
		productFolderConfiguration.setArchiveFolder(getValue(attributesConfiguration.getProductArchiveFolderAttribute(), values));
		productFolderConfiguration.setTempFolder(getValue(attributesConfiguration.getProductTempFolderAttribute(), values));
	}

	private void updatePeoFolderConfiguration(AttributesValues values)
	{
		peoFolderConfiguration.setInputFolder(getValue(attributesConfiguration.getPeoInputFolderAttribute(), values));
		peoFolderConfiguration.setSuccessFolder(getValue(attributesConfiguration.getPeoSuccessFolderAttribute(), values));
		peoFolderConfiguration.setErrorFolder(getValue(attributesConfiguration.getPeoErrorFolderAttribute(), values));
		peoFolderConfiguration.setArchiveFolder(getValue(attributesConfiguration.getPeoArchiveFolderAttribute(), values));
		peoFolderConfiguration.setTempFolder(getValue(attributesConfiguration.getPeoTempFolderAttribute(), values));
	}

	private void updateFilePatternConfiguration(AttributesValues values)
	{
		filePatternsConfiguration
			.setProjectFilePattern(getValue(attributesConfiguration.getProjectFilePatternAttribute(), values));
		filePatternsConfiguration.setPeoFilePattern(getValue(attributesConfiguration.getPeoFilePatternAttribute(), values));
		filePatternsConfiguration
			.setProductFilePattern(getValue(attributesConfiguration.getProductFilePatternAttribute(), values));
		filePatternsConfiguration.setPageFilePattern(getValue(attributesConfiguration.getPageFilePatternAttribute(), values));
	}

	private void updateScriptConfiguration(AttributesValues values)
	{
		scriptConfiguration
			.setWorkflowJsWebserviceUrl(getValue(attributesConfiguration.getWorkflowJsWebserviceUrlAttribute(), values));
		scriptConfiguration.setMandatorId(
			NumberUtils.createDouble(getValue(attributesConfiguration.getMandatorIdentityAttribute(), values)).longValue());
		scriptConfiguration
			.setNotificationScriptName(getValue(attributesConfiguration.getNotificationScriptNameAttribute(), values));
		scriptConfiguration.setCheckinScriptName(getValue(attributesConfiguration.getCheckinScriptNameAttribute(), values));
	}

	private String getValue(String attributeIdentifier, AttributesValues values)
	{
		AttributeValues attributeValue = values.get(attributeIdentifier);
		Assert.notNull(attributeValue, "Can't found value for attribute " + attributeIdentifier);
		return attributeValue.get(0).getValue();
	}

	private void rescheduleImport()
	{
		Scheduler scheduler = importSchedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(IMPORT_CRON_TRIGGER);
		try
		{
			CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
			trigger.setCronExpression(jobCronConfiguration.getCronExpression());
			Date nextExecutionDate = scheduler.rescheduleJob(triggerKey, trigger);
			LOGGER.info("Cron expression has been set to {}", jobCronConfiguration.getCronExpression());
			LOGGER.info("Next execution of import at {}", nextExecutionDate);
		}
		catch (ParseException e)
		{
			LOGGER.error("Wrong cron expression", e);
		}
		catch (SchedulerException e)
		{
			LOGGER.error("Can't reschedule import task with trigger name {}", IMPORT_CRON_TRIGGER, e);
		}
	}

	/**
	 * Get configuration project
	 * @return {@link Project}
	 * @throws RestException
	 *             config project can't be found by api
	 */
	private Project getConfigurationProject() throws RestException
	{
		LOGGER.info("Try to retrieve config project with id={}", commonConfiguration.getConfigurationProjectId());
		try
		{
			return projectApiService.getProjectById(commonConfiguration.getConfigurationProjectId());
		}
		catch (RestException e)
		{
			LOGGER.error("Can't find configuration project", e);
			throw e;
		}
	}


	/**
	 * Get configuration project
	 * @return {@link Project}
	 * @throws RestException
	 *             config project can't be found by api
	 */
	private void getLoginToken() throws RestException
	{
		LOGGER.info("Try to get new token");
		try
		{
			authService.getToken();
		}
		catch (RestException e)
		{
			LOGGER.error("Can't login to Server", e);
			throw e;
		}
	}
}
