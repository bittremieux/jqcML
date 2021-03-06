package inspector.jqcml.model;

/*
 * #%L
 * jqcML
 * %%
 * Copyright (C) 2013 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.base.MoreObjects;
import inspector.jqcml.jaxb.adapters.TableAttachmentAdapter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.IOException;

/**
 * Attachment containing additional information relevant for a certain {@link QualityParameter}.
 */
// JAXB
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="attachment")
@XmlType(name="AttachmentType")
//JPA
@Entity
@Table(name="attachment_parameter")
public class AttachmentParameter extends CvParameter {

    // JAXB
    @XmlTransient
    // JPA
    @Transient
    private static final Logger LOGGER = LogManager.getLogger(AttachmentParameter.class);

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
    protected AttachmentParameter() {
        super();
    }

    /**
     * Constructs a new AttachmentParameter object with the given name and id, and defined by the given {@link Cv} object.
     *
     * @param name  The name of the attachment, not {@code null}
     * @param cvRef  The reference to the Cv object which defines this attachment, not {@code null}
     * @param accession  The accession number identifying this parameter in the controlled vocabulary, not {@code null}
     * @param id  The unique identifier for this attachment, not {@code null}
     */
    public AttachmentParameter(String name, Cv cvRef, String accession, String id) {
        super(name, cvRef, accession);

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
     * @param id  The ID of this AttachmentParameter object, not {@code null}
     */
    private void setId(String id) {
        if(id != null) {
            this.id = id;
        } else {
            LOGGER.error("The AttachmentParameter's ID is not allowed to be <null>");
            throw new NullPointerException("The AttachmentParameter's ID is not allowed to be <null>");
        }
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
     * Stores the contents of a file as the binary information in this AttachmentParameter object.
     * AttachmentParameters can contain either binary data or tabular data, but not both at the same time.
     *
     * @param file  The file that will be stored as binary information as a base64-encoded string
     */
    public void setBinaryFromFile(File file) {
        try {
            binary = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));

        } catch(IOException e) {
            LOGGER.warn("Unable to read the specified file <{}> as a binary attachment", file.getAbsolutePath(), e);
            throw new IllegalArgumentException("Unable to read the specified file <" + file.getAbsolutePath() +  ">");
        }
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
        if(table != null) {
            // add the bi-directional relationship
            table.setParentAttachment(this);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("accession", accession).add("value", value)
                .add("unit name", unitName).add("unit accession", unitAccession).add("description", description)
                .add("binary", binary).add("table", table).omitNullValues().toString();
    }

}
