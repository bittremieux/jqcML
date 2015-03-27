package inspector.jqcml.model;

import inspector.jqcml.jpa.listener.QcDBParameterListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for parameters that are defined by a controlled vocabulary.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CVParamType")
@XmlSeeAlso( { MetaDataParameter.class, QualityParameter.class, AttachmentParameter.class } )
//JPA
@MappedSuperclass
@EntityListeners(QcDBParameterListener.class)
public class CvParameter extends AbstractParameter {

    // JAXB
    @XmlTransient
    // JPA
    @Transient
    private static final Logger LOGGER = LogManager.getLogger(CvParameter.class);

    /** (textual) reference to the controlled vocabulary used to define the parameter */
    // JAXB
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name="IDREF")
    @XmlAttribute(name="cvRef", required=true)
    // JPA
    @Transient
    protected String cvRefStr;
    /** (pointer) reference to the controlled vocabulary used to define the parameter */
    // JAXB
    @XmlTransient
    // JPA
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cv_ref", referencedColumnName="CV_ID_PK")
    protected Cv cvRef;
    /** the accession number identifying the definition of the parameter in the controlled vocabulary */
    // JAXB
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name="token")
    @XmlAttribute(required=true)
    // JPA
    @Column(name="accession", length=255)
    protected String accession;

    /**
     * Constructs a new empty CvParameter object.
     */
    public CvParameter() {
        super();
    }

    /**
     * Constructs a new CvParameter object with the given name and defined by the given {@link Cv} object.
     *
     * @param name  The name of the parameter
     * @param cvRef  The reference to the Cv object which defines this parameter
     */
    public CvParameter(String name, Cv cvRef) {
        setName(name);
        setCvRef(cvRef);
    }

    /**
     * Returns the ID of the {@link Cv} object representing the controlled vocabulary used to define this parameter.
     *
     * @return The ID of the Cv object which defines this parameter
     */
    public String getCvRefId() {
        return cvRefStr;
    }

    /**
     * Returns the {@link Cv} object representing the controlled vocabulary used to define this parameter.
     *
     * @return The Cv object which defines the parameter
     */
    public Cv getCvRef() {
        return cvRef;
    }

    /**
     * Sets the {@link Cv} object representing the controlled vocabulary used to define this parameter.
     *
     * This Cv object should be present in the root {@link QcML} object to which this parameter belongs.
     * If the Cv object is not present, an {@link IllegalArgumentException} is thrown.
     *
     * @param cv  The Cv object which defines this parameter. If cv is {@code null}, a {@link NullPointerException} is thrown.
     */
    public void setCvRef(Cv cv) {
        // check if the cv is in the root QcML object
        if(cv != null &&
                ((parentAssessment != null && parentAssessment.getParentQcML() != null && parentAssessment.getParentQcML().getCv(cv.getId()) != null) ||
                parentAssessment == null || parentAssessment.getParentQcML() == null)) {
            this.cvRef = cv;
            this.cvRefStr = cv.getId();
        } else if(cv == null) {
            LOGGER.error("It is not allowed to have a <null> cv reference for parameter {}", this);
            throw new NullPointerException("It is not allowed to have a <null> cv reference");
        } else {
            LOGGER.error("Can't add {} as cv reference to parameter {} because it is not present in the root qcML object", cv, this);
            throw new IllegalArgumentException("Can't add " + cv + " as cv reference because it is not present in the root qcML object");
        }
    }

    /**
     * Returns the accession number identifying this parameter in the controlled vocabulary.
     *
     * @return The accession number identifying this parameter in the controlled vocabulary
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the accession number identifying this parameter in the controlled vocabulary.
     *
     * @param accession  The accession number identifying this parameter in the controlled vocabulary
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    @Override
    public String toString() {
        return "cvParameter <name=\"" + getName() + "\", accession=\"" + getAccession() + "\">";
    }

}
