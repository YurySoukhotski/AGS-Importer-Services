/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ProjectLink
 */
public class ProjectLink
{
	@JsonProperty("href")
	private String href = null;
	@JsonProperty("projectId")
	private Long projectId = null;

	public ProjectLink href(String href)
	{
		this.href = href;
		return this;
	}

	/**
	 * Get href
	 * @return href
	 **/
	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public ProjectLink projectId(Long projectId)
	{
		this.projectId = projectId;
		return this;
	}

	/**
	 * Get projectId
	 * @return projectId
	 **/
	public Long getProjectId()
	{
		return projectId;
	}

	public void setProjectId(Long projectId)
	{
		this.projectId = projectId;
	}
}
