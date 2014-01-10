package inspector.jqcml.model;

import javax.persistence.Column;
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
 * Abstract base class for parameters containing some general information.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="abstractParameter")
@XmlTransient
@XmlSeeAlso( { CvParameter.class } )
// JPA
@MappedSuperclass
public abstract class AbstractParameter {
	
	// JAXB
	@XmlTransient
	// JPA
	@Transient
	protected static final Logger logger = LogManager.getLogger(AbstractParameter.class);

	/** the name of the parameter (as defined by the controlled vocabulary) */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name="token")
	@XmlAttribute(required=true)
	// JPA
	@Column(name="name", length=45)
	protected String name;
	/** the value of the parameter */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name="token")
	@XmlAttribute
	// JPA
	@Column(name="value", length=45)
	protected String value;
	/** the accession number identifying the unit in which the value is expressed in the controlled vocabulary */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name="token")
	@XmlAttribute
	// JPA
	@Column(name="unit_accession", length=45)
	protected String unitAccession;
	/** the name of the unit (as defined by the controlled vocabulary) in which the value is expressed */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name="token")
	@XmlAttribute
	// JPA
	@Column(name="unit_name", length=45)
	protected String unitName;
	/** (textual) reference to the controlled vocabulary used to define the unit in which the value is expressed */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name="IDREF")
	@XmlAttribute(name="unitCvRef")
	// JPA
	@Transient
	protected String unitCvRefStr;
	/** (pointer) reference to the controlled vocabulary used to define the unit in which the value is expressed */
	// JAXB
	@XmlTransient
	// JPA
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="unit_cv_ref", referencedColumnName="CV_ID_PK")
	protected Cv unitCvRef;

	/** inverse part of the bi-directional relationship with {@link QualityAssessment} */
	// JAXB
	@XmlTransient
	// JPA
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="QA_ID_FK", referencedColumnName="QA_ID_PK")
    protected QualityAssessment parentAssessment;

	/**
	 * Returns the name of this parameter (as defined by a controlled vocabulary).
	 * 
	 * @return The name of this parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this parameter.
	 * 
	 * @param name  The name of this parameter
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of this parameter.
	 * 
	 * @return The value of this parameter
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of this parameter.
	 * 
	 * @param value  The value of this parameter
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the accession number identifying the unit in which the value is expressed in the controlled vocabulary.
	 * 
	 * @return The accession number identifying the unit in the controlled vocabulary
	 */
	public String getUnitAccession() {
		return unitAccession;
	}

	/**
	 * Sets the accession number identifying the unit in which the value is expressed in the controlled vocabulary.
	 * 
	 * @param unitAccession  The accession number identifying the unit in the controlled vocabulary
	 */
	public void setUnitAccession(String unitAccession) {
		this.unitAccession = unitAccession;
	}

	/**
	 * Returns the name of the unit (as defined by the controlled vocabulary) in which the value is expressed.
	 * 
	 * @return The name of the unit in which the value is expressed
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * Sets the name of the unit (as defined by the controlled vocabulary) in which the value is expressed.
	 * 
	 * @param unitName  The name of the unit in which the value is expressed
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * Returns the ID of the {@link Cv} object representing the controlled vocabulary used to define the unit in which the value is expressed.
	 * 
	 * @return The ID of the Cv object which defines the unit
	 */
	public String getUnitCvRefId() {
		return unitCvRefStr;
	}

	/**
	 * Returns the {@link Cv} object representing the controlled vocabulary used to define the unit in which the value is expressed.
	 * 
	 * @return The Cv object which defines the unit
	 */
	public Cv getUnitCvRef() {
		return unitCvRef;
	}

	/**
	 * Sets the {@link Cv} object representing the controlled vocabulary used to define the unit in which the value is expressed.
	 * 
	 * This Cv object should be present in the root {@link QcML} object to which this parameter belongs.
	 * If the Cv object is not present, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param cv  The Cv object which defines the unit. If cv is {@code null}, the unit cv reference is reset (set to {@code null})
	 */
	public void setUnitCvRef(Cv cv) {
		// check if the cv is in the root QcML object
		if(cv != null &&
				((parentAssessment != null && parentAssessment.getParentQcML() != null && parentAssessment.getParentQcML().getCv(cv.getId()) != null) ||
				parentAssessment == null || parentAssessment.getParentQcML() == null)) {
			this.unitCvRef = cv;
			this.unitCvRefStr = cv.getId();
		}
		else if(cv == null) {
			this.unitCvRef = null;
			this.unitCvRefStr = null;
		}
		else {
			logger.error("Can't add {} as unit cv reference to parameter {} because it is not present in the root qcML object", cv, this);
			throw new IllegalArgumentException("Can't add " + cv + " as unit cv reference because it is not present in the root qcML object");
		}
	}
	
	/**
	 * Returns the parent {@link QualityAssessment} object in which this parameter is contained.
	 * 
	 * @return The parent QualityAssessment object 
	 */
	public QualityAssessment getParentQualityAssessment() {
		return parentAssessment;
	}
	
	/**
	 * Sets the parent {@link QualityAssessment} object in which this parameter is contained.
	 * 
	 * Make sure that when setting this relationship, this parameter effectively is to the parent QualityAssessment object.
	 * 
	 * @param parent  The parent QualityAssessment object
	 */
	public void setParentQualityAssessment(QualityAssessment parent) {
		this.parentAssessment = parent;
	}
	
	@Override
	public String toString() {
		return "abstractParameter <name=\"" + getName() + "\">";
	}

}
