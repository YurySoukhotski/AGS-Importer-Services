/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Link
 */
public class Link
{
	@JsonProperty("href")
	private String href = null;

	public Link href(String href)
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

}
