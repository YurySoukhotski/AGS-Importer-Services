//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.17 at 09:46:48 PM MSK 
//


package com.example.eximporter.importer.model.xml.project;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Project complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Project"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="marke" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Saison"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="HW 2013"/&gt;
 *               &lt;enumeration value="FS 2014"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MetaDataAttribute" type="{}MetaDataAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Expenses" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Project" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Project", propOrder = {"marke",
    "saison",
    "metaDataAttribute",
    "expenses"
})
public class Project
{

    @XmlElement(name = "Marke", required = true)
    protected String marke;
    @XmlElement(name = "Saison", required = true)
    protected String saison;
    @XmlElement(name = "MetaDataAttribute")
    protected List<MetaDataAttributeType> metaDataAttribute;
    @XmlElement(name = "Expenses", required = true)
    protected String expenses;
    @XmlAttribute(name = "ProjectType")
    protected String projectType;

    /**
     * Gets the value of the marke property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarke() {
        return marke;
    }

    /**
     * Sets the value of the marke property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarke(String value) {
        this.marke = value;
    }

    /**
     * Gets the value of the saison property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaison() {
        return saison;
    }

    /**
     * Sets the value of the saison property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaison(String value) {
        this.saison = value;
    }

    /**
     * Gets the value of the metaDataAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metaDataAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetaDataAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetaDataAttributeType }
     * 
     * 
     */
    public List<MetaDataAttributeType> getMetaDataAttribute() {
        if (metaDataAttribute == null) {
            metaDataAttribute = new ArrayList<>();
        }
        return this.metaDataAttribute;
    }

    /**
     * Gets the value of the expenses property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpenses() {
        return expenses;
    }

    /**
     * Sets the value of the expenses property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpenses(String value) {
        this.expenses = value;
    }

    /**
     * Gets the value of the projectType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * Sets the value of the projectType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectType(String value) {
        this.projectType = value;
    }

    public void setMetaDataAttribute(List<MetaDataAttributeType> metaDataAttribute)
    {
        this.metaDataAttribute = metaDataAttribute;
    }
}
