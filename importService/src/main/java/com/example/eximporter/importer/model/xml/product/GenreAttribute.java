//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.18 at 01:46:47 PM MSK 
//


package com.example.eximporter.importer.model.xml.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="genreAttrName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="genreAttrId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class GenreAttribute {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "genreAttrName")
    protected String genreAttrName;
    @XmlAttribute(name = "genreAttrId")
    protected String genreAttrId;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the genreAttrName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenreAttrName() {
        return genreAttrName;
    }

    /**
     * Sets the value of the genreAttrName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenreAttrName(String value) {
        this.genreAttrName = value;
    }

    /**
     * Gets the value of the genreAttrId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenreAttrId() {
        return genreAttrId;
    }

    /**
     * Sets the value of the genreAttrId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenreAttrId(String value) {
        this.genreAttrId = value;
    }

}
