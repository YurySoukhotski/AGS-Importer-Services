package com.example.eximporter.importer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScriptConfiguration
{
	@Value("${default.workflow.js.webservice.url}")
	private String workflowJsWebserviceUrl;
	@Value("${default.mandator.id}")
	private Long mandatorId;
	@Value("${default.notification.script.name}")
	private String notificationScriptName;
	@Value("${default.checkin.script.name}")
	private String checkinScriptName;

	/**
	 * Gets WorkflowJavascriptWebservice url
	 * @return WorkflowJavascriptWebservice url
	 */
	public String getWorkflowJsWebserviceUrl()
	{
		return workflowJsWebserviceUrl;
	}

	/**
	 * Sets WorkflowJavascriptWebservice url
	 * @param workflowJsWebserviceUrl
	 *            WorkflowJavascriptWebservice url
	 */
	public void setWorkflowJsWebserviceUrl(String workflowJsWebserviceUrl)
	{
		this.workflowJsWebserviceUrl = workflowJsWebserviceUrl;
	}

	/**
	 * Gets mandator identity
	 * @return mandator identity
	 */
	public Long getMandatorId()
	{
		return mandatorId;
	}

	/**
	 * Sets mandator identity
	 * @param mandatorId
	 *            mandator identity
	 */
	public void setMandatorId(Long mandatorId)
	{
		this.mandatorId = mandatorId;
	}

	/**
	 * Gets notification script name
	 * @return name of notification script
	 */
	public String getNotificationScriptName()
	{
		return notificationScriptName;
	}

	/**
	 * Sets notification script name
	 * @param notificationScriptName
	 *            name of notification script
	 */
	public void setNotificationScriptName(String notificationScriptName)
	{
		this.notificationScriptName = notificationScriptName;
	}

	/**
	 * Gets checkin script name
	 * @return name of checkin script
	 */
	public String getCheckinScriptName()
	{
		return checkinScriptName;
	}

	/**
	 * Sets checkin script name
	 * @param checkinScriptName
	 *            name of checkin script
	 */
	public void setCheckinScriptName(String checkinScriptName)
	{
		this.checkinScriptName = checkinScriptName;
	}
}
