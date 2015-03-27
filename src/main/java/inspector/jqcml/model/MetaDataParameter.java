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

import com.google.common.base.MoreObjects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Metadata parameter. A metadata parameter contains metadata about the {@link QualityParameter}s.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="metaDataParameter")
@XmlType(name="MetaDataType")
//JPA
@Entity
@Table(name="meta_data_parameter")
public class MetaDataParameter extends CvParameter {

    // JAXB
    @XmlTransient
    // JPA
    @Transient
    private static final Logger LOGGER = LogManager.getLogger(MetaDataParameter.class);

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
     * Constructs a new empty MetaDataParameter object.
     */
    protected MetaDataParameter() {
        super();
    }

    /**
     * Constructs a new MetaDataParameter object with the given name and id, and defined by the given {@link Cv} object.
     *
     * @param name  The name of the parameter
     * @param cvRef  The reference to the Cv object which defines this parameter
     * @param accession  The accession number identifying this parameter in the controlled vocabulary
     * @param id  The unique identifier for this parameter
     */
    public MetaDataParameter(String name, Cv cvRef, String accession, String id) {
        super(name, cvRef, accession);

        setId(id);
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
    private void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("accession", accession).add("value", value)
                .add("unit name", unitName).add("unit accession", unitAccession).add("description", description)
                .omitNullValues().toString();
    }
}
