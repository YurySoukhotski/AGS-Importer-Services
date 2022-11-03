/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Link to projects related to peo
 */
public class ProjectRequest {
	@JsonProperty("projectId")
	private Long projectId = null;

	public ProjectRequest projectId(Long projectId) {
		this.projectId = projectId;
		return this;
	}

	/**
	 * Get projectId
	 *
	 * @return projectId
	 **/
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}
