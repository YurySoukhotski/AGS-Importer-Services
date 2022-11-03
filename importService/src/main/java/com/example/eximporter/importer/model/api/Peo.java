/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Product export option object(PEO). Includes attribute values
 */
public class Peo
{
	@JsonProperty("id")
	private Long id = null;
	@JsonProperty("name")
	private String name = null;
	@JsonProperty("productId")
	private Long productId = null;
	@JsonProperty("templateId")
	private Long templateId = null;
	@JsonProperty("attributes")
	private AttributesValues attributes = null;
	@JsonProperty("tableAttributes")
	private TableAttributesValues tableAttributes = null;

	public Peo id(Long id)
	{
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * @return id
	 **/
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Peo name(String name)
	{
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * @return name
	 **/
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Peo productId(Long productId)
	{
		this.productId = productId;
		return this;
	}

	/**
	 * Get productId
	 * @return productId
	 **/
	public Long getProductId()
	{
		return productId;
	}

	public void setProductId(Long productId)
	{
		this.productId = productId;
	}

	public Peo templateId(Long templateId)
	{
		this.templateId = templateId;
		return this;
	}

	/**
	 * Get templateId
	 * @return templateId
	 **/
	public Long getTemplateId()
	{
		return templateId;
	}

	public void setTemplateId(Long templateId)
	{
		this.templateId = templateId;
	}

	public Peo attributes(AttributesValues attributes)
	{
		this.attributes = attributes;
		return this;
	}

	/**
	 * Get attributes
	 * @return attributes
	 **/
	public AttributesValues getAttributes()
	{
		return attributes;
	}

	public void setAttributes(AttributesValues attributes)
	{
		this.attributes = attributes;
	}

	public Peo tableAttributes(TableAttributesValues tableAttributes)
	{
		this.tableAttributes = tableAttributes;
		return this;
	}

	/**
	 * Get tableAttributes
	 * @return tableAttributes
	 **/
	public TableAttributesValues getTableAttributes()
	{
		return tableAttributes;
	}

	public void setTableAttributes(TableAttributesValues tableAttributes)
	{
		this.tableAttributes = tableAttributes;
	}
	
}
