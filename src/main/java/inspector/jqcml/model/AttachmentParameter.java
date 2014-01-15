package inspector.jqcml.model;

import inspector.jqcml.jaxb.adapters.TableAttachmentAdapter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Attachment containing additional information relevant for a certain {@link QualityParameter}.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="attachment")
@XmlType(name="attachmentType")
//JPA
@Entity
@Table(name="attachment_parameter")
public class AttachmentParameter extends CvParameter {
	
	// JAXB
	@XmlTransient
	// JPA
	@Transient
	private static final Logger logger = LogManager.getLogger(AttachmentParameter.class);

	/** read-only qcDB primary key; generated by JPA */
	// JAXB
	@XmlTransient
	// JPA
	@Id
	@TableGenerator(name="pk_attachment", table="pk_sequence", pkColumnName="name",
			valueColumnName="seq", pkColumnValue="attachment_parameter", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="pk_attachment")
	@Column(name="AP_ID_PK")
	private int primaryKey;
	
	/** a unique identifier */
	// JAXB
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name="ID")
	@XmlID
	@XmlAttribute(name="ID", required=true)
	// JPA
	@Column(name="id", length=255, unique=true)
	private String id;
	/** reference to the {@link QualityParameter} for which this attachment contains additional information */
	// JAXB
    @XmlSchemaType(name="IDREF")
	@XmlIDREF
	@XmlAttribute
	// JPA
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="quality_parameter_ref", referencedColumnName="id")
	private QualityParameter qualityParameterRef;
    /** binary data as a base64 string */
	// JAXB
	@XmlSchemaType(name="base64Binary")
	@XmlElement
	// JPA
	@Lob
	@Column(name="binary_blob")
	private String binary;
	/** tabular data */
	// JAXB
	@XmlJavaTypeAdapter(TableAttachmentAdapter.class)
	@XmlPath("table")
	// JPA
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentAttachment")
	private TableAttachment table;
	
	/**
     * Constructs a new empty AttachmentParameter object.
     */
	public AttachmentParameter() {
		super();
	}
	
	/**
	 * Constructs a new AttachmentParameter object with the given name and id, and defined by the given {@link Cv} object.
	 * 
	 * @param name  The name of the attachment
	 * @param cvRef  The reference to the Cv object which defines this attachment
	 * @param id  The unique identifier for this attachment
	 */
	public AttachmentParameter(String name, Cv cvRef, String id) {
		super(name, cvRef);
		
		setId(id);
	}
	
	/**
	 * Returns the primary key of this AttachmentParameter object used in the qcDB.
	 * 
	 * The primary key is (generally) read-only; the qcDB implementation will generate a suitable primary key when required.
	 * If this AttachmentParameter object isn't connected to a certain qcDB, the primary key will not be set.
	 * 
	 * @return The primary key of this AttachmentParameter object
	 */
	public int getPrimaryKey() {
		return primaryKey;
	}
	
	/**
	 * Returns the unique identifier of this AttachmentParameter object.
	 * 
	 * @return  The ID of this AttachmentParameter object
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the unique identifier of this AttachmentParameter object.
	 * 
	 * @param id  The ID of this AttachmentParameter object
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the {@link QualityParameter} object for which this attachment contains additional information.
	 * 
	 * @return The QualityParameter for which this attachment contains additional information
	 */
	public QualityParameter getQualityParameterRef() {
		return qualityParameterRef;
	}

	/**
	 * Sets the {@link QualityParameter} object for which this attachment contains additional information.
	 * 
	 * @param qualityParameterRef  The QualityParameter object for which this attachment contains additional information
	 */
	public void setQualityParameterRef(QualityParameter qualityParameterRef) {
		this.qualityParameterRef = qualityParameterRef;
	}

	/**
	 * Returns the binary information in this AttachmentParameter object as a base64-encoded string.
	 * 
	 * @return The binary information as a base64-encoded string
	 */
	public String getBinary() {
		return binary;
	}

	/**
	 * Sets the binary information in this AttachmentParameter object.
	 * AttachmentParameters can contain either binary data or tabular data, but not both at the same time.
	 * 
	 * @param binary  The binary information as a base64-encoded string
	 */
	public void setBinary(String binary) {
		this.binary = binary;
	}
	
	/**
	 * Returns the tabular data in this AttachmentParameter object.
	 * 
	 * @return A reference to the tabular data
	 */
	public TableAttachment getTable() {
		return table;
	}
	
	/**
	 * Sets the tabular information in this AttachmentParameter object.
	 * AttachmentParameters can contain either binary data or tabular data, but not both at the same time.
	 * 
	 * @param table  A reference to the tabular data 
	 */
	public void setTable(TableAttachment table) {
		this.table = table;
        if(table != null)
            table.setParentAttachment(this);	// add the bi-directional relationship
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		else if(other == this)
			return true;
		else if(!(other instanceof AttachmentParameter))
			return false;
		else {
			AttachmentParameter apOther = (AttachmentParameter) other;
			boolean isEqual = true;
			if(getName() != null) isEqual &= getName().equals(apOther.getName()); else isEqual &= apOther.getName() == null;
			if(getValue() != null) isEqual &= getValue().equals(apOther.getValue()); else isEqual &= apOther.getValue() == null;
			if(getUnitAccession() != null) isEqual &= getUnitAccession().equals(apOther.getUnitAccession()); else isEqual &= apOther.getUnitAccession() == null;
			if(getUnitName() != null) isEqual &= getUnitName().equals(apOther.getUnitName()); else isEqual &= apOther.getUnitName() == null;
			if(getUnitCvRef() != null) isEqual &= getUnitCvRef().equals(apOther.getUnitCvRef()); else isEqual &= apOther.getUnitCvRef() == null;
			if(getCvRef() != null) isEqual &= getCvRef().equals(apOther.getCvRef()); else isEqual &= apOther.getCvRef() == null;
			if(getAccession() != null) isEqual &= getAccession().equals(apOther.getAccession()); else isEqual &= apOther.getAccession() == null;
			if(getId() != null) isEqual &= getId().equals(apOther.getId()); else isEqual &= apOther.getId() == null;
			if(getQualityParameterRef() != null) isEqual &= getQualityParameterRef().equals(apOther.getQualityParameterRef()); else isEqual &= apOther.getQualityParameterRef() == null;
			if(getBinary() != null) isEqual &= getBinary().equals(apOther.getBinary()); else isEqual &= apOther.getBinary() == null;
			if(getTable() != null) isEqual &= getTable().equals(apOther.getTable()); else isEqual &= apOther.getTable() == null;
			
			return isEqual;
		}
	}
	
	@Override
	public String toString() {
		return "attachmentParameter <ID=\"" + getId() + "\" name=\"" + getName() + "\">";
	}

}
