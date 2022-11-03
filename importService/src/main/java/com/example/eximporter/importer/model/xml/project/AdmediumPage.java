//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.18 at 01:57:36 PM MSK
//
package com.example.eximporter.importer.model.xml.project;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="pageTechId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pageKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="workPageNum" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="admediumVersionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@Entity
@Table(name = "EX_IMPORTER_PARKED_PAGE")
public class AdmediumPage
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long attempt;
	private String fileName;
	@XmlAttribute(name = "pageTechId")
	protected String pageTechId;
	@XmlAttribute(name = "pageKey")
	protected String pageKey;
	@XmlAttribute(name = "workPageNum")
	protected String workPageNum;
	@XmlAttribute(name = "workPageLabel")
	protected String workPageLabel;
	@XmlAttribute(name = "admediumVersionId")
	protected String admediumVersionId;
	@XmlAttribute(name = "isMaster")
	protected Boolean isMaster;

	/**
	 * Gets the value of the pageTechId property.
	 * @return possible object is {@link String }
	 */
	public String getPageTechId()
	{
		return pageTechId;
	}

	/**
	 * Sets the value of the pageTechId property.
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setPageTechId(String value)
	{
		this.pageTechId = value;
	}

	/**
	 * Gets the value of the pageKey property.
	 * @return possible object is {@link String }
	 */
	public String getPageKey()
	{
		return pageKey;
	}

	/**
	 * Sets the value of the pageKey property.
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setPageKey(String value)
	{
		this.pageKey = value;
	}

	/**
	 * Gets the value of the workPageNum property.
	 * @return possible object is {@link String }
	 */
	public String getWorkPageNum()
	{
		return workPageNum;
	}

	/**
	 * Sets the value of the workPageNum property.
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setWorkPageNum(String value)
	{
		this.workPageNum = value;
	}

	/**
	 * Gets the value of the admediumVersionId property.
	 * @return possible object is {@link String }
	 */
	public String getAdmediumVersionId()
	{
		return admediumVersionId;
	}

	/**
	 * Sets the value of the admediumVersionId property.
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setAdmediumVersionId(String value)
	{
		this.admediumVersionId = value;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getAttempt()
	{
		return attempt;
	}

	public void setAttempt(Long attempt)
	{
		this.attempt = attempt;
	}

    public String getWorkPageLabel()
    {
        return workPageLabel;
    }

    public void setWorkPageLabel(String workPageLabel)
    {
        this.workPageLabel = workPageLabel;
    }

	public Boolean getMaster() {
		return isMaster;
	}

	public void setMaster(Boolean master) {
		isMaster = master;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
}