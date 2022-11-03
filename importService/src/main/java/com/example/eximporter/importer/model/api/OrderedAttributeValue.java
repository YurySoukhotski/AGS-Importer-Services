/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OrderedAttributeValue
 */
public class OrderedAttributeValue
{
	@JsonProperty("value")
	private String value = null;
	@JsonProperty("lang")
	private String lang = null;
	@JsonProperty("ordernum")
	private Long ordernum = null;

	public OrderedAttributeValue value(String value)
	{
		this.value = value;
		return this;
	}

	/**
	 * Get value
	 * @return value
	 **/
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public OrderedAttributeValue lang(String lang)
	{
		this.lang = lang;
		return this;
	}

	/**
	 * Get lang
	 * @return lang
	 **/
	public String getLang()
	{
		return lang;
	}

	public void setLang(String lang)
	{
		this.lang = lang;
	}

	public OrderedAttributeValue ordernum(Long ordernum)
	{
		this.ordernum = ordernum;
		return this;
	}

	/**
	 * Get ordernum
	 * @return ordernum
	 **/
	public Long getOrdernum()
	{
		return ordernum;
	}

	public void setOrdernum(Long ordernum)
	{
		this.ordernum = ordernum;
	}

}
