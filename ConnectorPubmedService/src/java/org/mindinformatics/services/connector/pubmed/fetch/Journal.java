//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.06.03 at 01:03:34 AM EDT 
//


package org.mindinformatics.services.connector.pubmed.fetch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}ISSN" minOccurs="0"/>
 *         &lt;element ref="{}JournalIssue"/>
 *         &lt;element ref="{}Coden" minOccurs="0"/>
 *         &lt;element ref="{}Title" minOccurs="0"/>
 *         &lt;element ref="{}ISOAbbreviation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "issn",
    "journalIssue",
    "coden",
    "title",
    "isoAbbreviation"
})
@XmlRootElement(name = "Journal")
public class Journal {

    @XmlElement(name = "ISSN")
    protected ISSN issn;
    @XmlElement(name = "JournalIssue", required = true)
    protected JournalIssue journalIssue;
    @XmlElement(name = "Coden")
    protected Coden coden;
    @XmlElement(name = "Title")
    protected Title title;
    @XmlElement(name = "ISOAbbreviation")
    protected ISOAbbreviation isoAbbreviation;

    /**
     * Gets the value of the issn property.
     * 
     * @return
     *     possible object is
     *     {@link ISSN }
     *     
     */
    public ISSN getISSN() {
        return issn;
    }

    /**
     * Sets the value of the issn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ISSN }
     *     
     */
    public void setISSN(ISSN value) {
        this.issn = value;
    }

    /**
     * Gets the value of the journalIssue property.
     * 
     * @return
     *     possible object is
     *     {@link JournalIssue }
     *     
     */
    public JournalIssue getJournalIssue() {
        return journalIssue;
    }

    /**
     * Sets the value of the journalIssue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalIssue }
     *     
     */
    public void setJournalIssue(JournalIssue value) {
        this.journalIssue = value;
    }

    /**
     * Gets the value of the coden property.
     * 
     * @return
     *     possible object is
     *     {@link Coden }
     *     
     */
    public Coden getCoden() {
        return coden;
    }

    /**
     * Sets the value of the coden property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coden }
     *     
     */
    public void setCoden(Coden value) {
        this.coden = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link Title }
     *     
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link Title }
     *     
     */
    public void setTitle(Title value) {
        this.title = value;
    }

    /**
     * Gets the value of the isoAbbreviation property.
     * 
     * @return
     *     possible object is
     *     {@link ISOAbbreviation }
     *     
     */
    public ISOAbbreviation getISOAbbreviation() {
        return isoAbbreviation;
    }

    /**
     * Sets the value of the isoAbbreviation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ISOAbbreviation }
     *     
     */
    public void setISOAbbreviation(ISOAbbreviation value) {
        this.isoAbbreviation = value;
    }

}