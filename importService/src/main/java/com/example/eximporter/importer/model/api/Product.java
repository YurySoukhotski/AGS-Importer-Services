/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2017 apollon GmbH+Co. KG All Rights Reserved.
 * @author yury.soukhotski
 */

public class Product
{
	@JsonProperty("id")
	private Long id = null;
	@JsonProperty("parentId")
	private Long parentId = null;
	@JsonProperty("path")
	private String path = null;
	@JsonProperty("name")
	private String name = null;
	@JsonProperty("type")
	private String type = null;
	@JsonProperty("keywordPath")
	private String keywordPath = null;
	@JsonProperty("attributes")
	private AttributesValues attributes = null;
	@JsonProperty("tableAttributes")
	private TableAttributesValues tableAttributes = null;

	public Product id(Long id)
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

	public Product parentId(Long parentId)
	{
		this.parentId = parentId;
		return this;
	}

	/**
	 * Get parentId
	 * @return parentId
	 **/

	public Long getParentId()
	{
		return parentId;
	}

	public void setParentId(Long parentId)
	{
		this.parentId = parentId;
	}

	public Product path(String path)
	{
		this.path = path;
		return this;
	}

	/**
	 * Get path
	 * @return path
	 **/

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public Product name(String name)
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

	public Product type(String type)
	{
		this.type = type;
		return this;
	}

	/**
	 * Get type
	 * @return type
	 **/

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Product attributes(AttributesValues attributes)
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

	public Product tableAttributes(TableAttributesValues tableAttributes)
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

	/**
	 * Get keyword path to link product
	 * @return keyword path
	 */
	public String getKeywordPath()
	{
		return keywordPath;
	}

	public void setKeywordPath(String keywordPath)
	{
		this.keywordPath = keywordPath;
	}

}
