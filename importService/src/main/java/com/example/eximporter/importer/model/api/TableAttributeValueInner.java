/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TableAttributeValueInner
 */
public class TableAttributeValueInner
{
	@JsonProperty("rownum")
	private Long rownum = null;
	@JsonProperty("cells")
	private AttributesValues cells = null;

	/**
	 * Get rownum
	 * @return rownum
	 **/
	public Long getRownum()
	{
		return rownum;
	}

	public void setRownum(Long rownum)
	{
		this.rownum = rownum;
	}

	public TableAttributeValueInner cells(AttributesValues cells)
	{
		this.cells = cells;
		return this;
	}

	/**
	 * Get cells
	 * @return cells
	 **/
	public AttributesValues getCells()
	{
		return cells;
	}

	public void setCells(AttributesValues cells)
	{
		this.cells = cells;
	}
}
