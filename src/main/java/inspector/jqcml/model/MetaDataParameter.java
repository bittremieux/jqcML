package inspector.jqcml.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Metadata parameter. A metadata parameter contains metadata about the {@link QualityParameter}s.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="metaDataParameter")
@XmlType(name="metaDataType")
//JPA
@Entity
@Table(name="metadata_parameter")
public class MetaDataParameter extends CvParameter {

	// JAXB
	@XmlTransient
	// JPA
	@Transient
	private static final Logger logger = LogManager.getLogger(MetaDataParameter.class);

	/** read-only qcDB primary key; generated by JPA */
	// JAXB
	@XmlTransient
	// JPA
	@Id
	@TableGenerator(name="pk_mp", table="pk_sequence", pkColumnName="name",
			valueColumnName="seq", pkColumnValue="metadata_parameter", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="pk_mp")
	@Column(name="MP_ID_PK")
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

	/**
     * Constructs a new empty QualityParameter object.
     */
	public MetaDataParameter() {
		super();
	}
	
	/**
	 * Returns the primary key of this MetaDataParameter object used in the qcDB.
	 * 
	 * The primary key is (generally) read-only; the qcDB implementation will generate a suitable primary key when required.
	 * If this MetaDataParameter object isn't connected to a certain qcDB, the primary key will not be set.
	 * 
	 * @return The primary key of this MetaDataParameter object
	 */
	public int getPrimaryKey() {
		return primaryKey;
	}
	
	/**
	 * Returns the unique identifier of this MetaDataParameter object.
	 * 
	 * @return The ID of this MetaDataParameter object
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the unique identifier of this MetaDataParameter object.
	 * 
	 * @param id  The ID of this MetaDataParameter object
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		else if(other == this)
			return true;
		else if(!(other instanceof MetaDataParameter))
			return false;
		else {
			MetaDataParameter mpOther = (MetaDataParameter) other;
			boolean isEqual = true;
			if(getName() != null) isEqual &= getName().equals(mpOther.getName()); else isEqual &= mpOther.getName() == null;
			if(getDescription() != null) isEqual &= getDescription().equals(mpOther.getDescription()); else isEqual &= mpOther.getDescription() == null;
			if(getValue() != null) isEqual &= getValue().equals(mpOther.getValue()); else isEqual &= mpOther.getValue() == null;
			if(getUnitAccession() != null) isEqual &= getUnitAccession().equals(mpOther.getUnitAccession()); else isEqual &= mpOther.getUnitAccession() == null;
			if(getUnitName() != null) isEqual &= getUnitName().equals(mpOther.getUnitName()); else isEqual &= mpOther.getUnitName() == null;
			if(getUnitCvRef() != null) isEqual &= getUnitCvRef().equals(mpOther.getUnitCvRef()); else isEqual &= mpOther.getUnitCvRef() == null;
			if(getCvRef() != null) isEqual &= getCvRef().equals(mpOther.getCvRef()); else isEqual &= mpOther.getCvRef() == null;
			if(getAccession() != null) isEqual &= getAccession().equals(mpOther.getAccession()); else isEqual &= mpOther.getAccession() == null;
			if(getId() != null) isEqual &= getId().equals(mpOther.getId()); else isEqual &= mpOther.getId() == null;

			return isEqual;
		}
	}
	
	@Override
	public String toString() {
		return "metadataParameter <ID=\"" + getId() + "\" name=\"" + getName() + "\" value=\"" + getValue() + "\">";
	}
	
	

}
