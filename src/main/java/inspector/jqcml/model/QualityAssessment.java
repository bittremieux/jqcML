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
import inspector.jqcml.jaxb.adapters.QualityAssessmentAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * The container grouping several {@link QualityParameter}s.
 * 
 * A QualityAssessment can signify both a runQuality and a setQuality.
 * A runQuality contains information about a single mass spectrometry experiment.
 * A setQuality contains aggregated information about several runQualities.
 */
// JAXB
// member variables don't need further JAXB annotations (these are included in the QualityAssessmentList class)
@XmlJavaTypeAdapter(QualityAssessmentAdapter.class)
//JPA
@Entity
@Table(name="quality_assessment")
public class QualityAssessment {

    @Transient
    private static final Logger LOGGER = LogManager.getLogger(QualityAssessment.class);

    /** read-only qcDB primary key; generated by JPA */
    @Id
    @TableGenerator(name="pk_qa", table="pk_sequence", pkColumnName="name",
            valueColumnName="seq", pkColumnValue="quality_assessment", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="pk_qa")
    @Column(name="QA_ID_PK")
    private int primaryKey;

    /** a unique identifier */
    @Column(name="id", length=255, unique=true)
    private String id;
    /** list of {@link MetaDataParameter}s */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentAssessment")
    @MapKey(name="accession")
    // key=accession, value=QP
    private Map<String, MetaDataParameter> metaDataList;
    /** list of {@link QualityParameter}s */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentAssessment")
    @MapKey(name="accession")
    // key=accession, value=QP
    private Map<String, QualityParameter> parameterList;
    /** list of {@link AttachmentParameter}s */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentAssessment")
    @MapKey(name="accession")
    // key=accession, value=AP
    private Map<String, AttachmentParameter> attachmentList;
    /** flag indicating whether this is a runQuality or a setQuality */
    @Column(name="is_set")
    private boolean isSet;

    /** inverse part of the bi-directional relationship with {@link QcML} */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="QC_ID_FK", referencedColumnName="QC_ID_PK")
    private QcML parentQcML;
    
    /**
     * Constructs a new QualityAssessment object with empty {@link QualityParameter} and {@link AttachmentParameter} lists.
     */
    protected QualityAssessment() {
        this.metaDataList = new TreeMap<>();
        this.parameterList = new TreeMap<>();
        this.attachmentList = new TreeMap<>();
    }

    /**
     * Constructs a new QualityAssessment object, with the specified ID, with empty {@link QualityParameter} and {@link AttachmentParameter} lists.
     * 
     * @param id  The ID of this QualityAssessment object
     * @param isSet  Indicates whether this QualityAssessment object represents a RunQuality or a SetQuality
     */
    public QualityAssessment(String id, boolean isSet) {
        this();

        setId(id);
        setSet(isSet);
    }

    /**
     * Returns the primary key of this QualityAssessment object used in the qcDB.
     *
     * The primary key is read-only; the qcDB implementation will generate a suitable primary key when required.
     * If this QualityAssessment object isn't connected to a certain qcDB, the primary key will not be set.
     *
     * @return The primary key of this QualityAssessment object
     */
    public int getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Returns the unique identifier of this QualityAssessment object.
     *
     * @return  The ID of this QualityAssessment object
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this QualityAssessment object.
     *
     * @param id  The ID of this QualityAssessment object
     */
    private void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the number of {@link MetaDataParameter}s that are contained in this QualityAssessment object.
     *
     * @return The number of MetaDataParameters
     */
    public int getNumberOfMetaDataParameters() {
        return metaDataList.size();
    }

    /**
     * Returns the {@link MetaDataParameter} specified by the given accession number.
     *
     * @param accession  The accession number of the requested MetaDataParameter
     * @return The MetaDataParameter specified by the given accession number if this MetaDataParameter is present, {@code null} otherwise
     */
    public MetaDataParameter getMetaDataParameter(String accession) {
        return accession != null ? metaDataList.get(accession) : null;
    }

    /**
     * Returns a {@link Iterator} over all {@link MetaDataParameter}s contained in this QualityAssessment object.
     *
     * @return An Iterator over all MetaDataParameters
     */
    public Iterator<MetaDataParameter> getMetaDataParameterIterator() {
        return metaDataList.values().iterator();
    }

    /**
     * Adds a given {@link MetaDataParameter} to this QualityAssessment object.
     *
     * If a MetaDataParameter with the same accession number was already present, the old MetaDataParameter is replaced by the given MetaDataParameter.
     *
     * @param param  The given MetaDataParameter
     */
    public void addMetaDataParameter(MetaDataParameter param) {
        if(param != null) {
            // add the bi-directional relationship
            param.setParentQualityAssessment(this);
            metaDataList.put(param.getAccession(), param);
        } else {
            LOGGER.error("Can't add <null> MetaDataParameter to a QualityAssessment object");
            throw new NullPointerException("Can't add <null> MetaDataParameter");
        }
    }

    /**
     * Removes the {@link MetaDataParameter} contained in this QualityAssessment object, specified by the given accession number, from this QualityAssessment object.
     *
     * @param accession  The accession number of the MetaDataParameter that will be removed
     */
    public void removeMetaDataParameter(String accession) {
        if(accession != null) {
            MetaDataParameter param = getMetaDataParameter(accession);
            if(param != null) {
                // remove the bi-directional relationship
                param.setParentQualityAssessment(null);
            }
            metaDataList.remove(accession);
        }
    }

    /**
     * Removes all {@link MetaDataParameter}s contained in this QualityAssessment object, from this QualityAssessment object.
     */
    public void removeAllMetaDataParameters() {
        Iterator<MetaDataParameter> it = getMetaDataParameterIterator();
        while(it.hasNext()) {
            // first remove the bi-directional relationship
            MetaDataParameter param = it.next();
            param.setParentQualityAssessment(null);
            // remove the QualityParameter
            it.remove();
        }
    }

    /**
     * Returns the number of {@link QualityParameter}s that are contained in this QualityAssessment object.
     *
     * @return The number of QualityParameters
     */
    public int getNumberOfQualityParameters() {
        return parameterList.size();
    }

    /**
     * Returns the {@link QualityParameter} specified by the given accession number.
     *
     * @param accession  The accession number of the requested QualityParameter
     * @return The QualityParameter specified by the given accession number if this QualityParameter is present, {@code null} otherwise
     */
    public QualityParameter getQualityParameter(String accession) {
        return accession != null ? parameterList.get(accession) : null;
    }

    /**
     * Returns a {@link Iterator} over all {@link QualityParameter}s contained in this QualityAssessment object.
     *
     * @return An Iterator over all QualityParameters
     */
    public Iterator<QualityParameter> getQualityParameterIterator() {
        return parameterList.values().iterator();
    }

    /**
     * Adds a given {@link QualityParameter} to this QualityAssessment object.
     *
     * If a QualityParameter with the same accession number was already present, the old QualityParameter is replaced by the given QualityParameter.
     *
     * @param param  The given QualityParameter
     */
    public void addQualityParameter(QualityParameter param) {
        if(param != null) {
            // add the bi-directional relationship
            param.setParentQualityAssessment(this);
            parameterList.put(param.getAccession(), param);
        } else {
            LOGGER.error("Can't add <null> QualityParameter to a QualityAssessment object");
            throw new NullPointerException("Can't add <null> QualityParameter");
        }
    }

    /**
     * Removes the {@link QualityParameter} contained in this QualityAssessment object, specified by the given accession number, from this QualityAssessment object.
     *
     * @param accession  The accession number of the QualityParameter that will be removed
     */
    public void removeQualityParameter(String accession) {
        if(accession != null) {
            QualityParameter param = getQualityParameter(accession);
            if(param != null) {
                // remove the bi-directional relationship
                param.setParentQualityAssessment(null);
            }
            parameterList.remove(accession);
        }
    }

    /**
     * Removes all {@link QualityParameter}s contained in this QualityAssessment object, from this QualityAssessment object.
     */
    public void removeAllQualityParameters() {
        Iterator<QualityParameter> it = getQualityParameterIterator();
        while(it.hasNext()) {
            // first remove the bi-directional relationship
            QualityParameter param = it.next();
            param.setParentQualityAssessment(null);
            // remove the QualityParameter
            it.remove();
        }
    }

    /**
     * Returns the number of {@link AttachmentParameter}s that are contained in this QualityAssessment object.
     *
     * @return The number of AttachmentParameters
     */
    public int getNumberOfAttachmentParameters() {
        return attachmentList.size();
    }

    /**
     * Returns the {@link AttachmentParameter} specified by the given accession number.
     *
     * @param accession  The accession number of the requested AttachmentParameter
     * @return The AttachmentParameter specified by the given accession number if this AttachmentParameter is present, {@code null} otherwise
     */
    public AttachmentParameter getAttachmentParameter(String accession) {
        return accession != null ? attachmentList.get(accession) : null;
    }

    /**
     * Returns a {@link Iterator} over all {@link AttachmentParameter}s contained in this QualityAssessment object.
     *
     * @return An Iterator over all AttachmentParameters
     */
    public Iterator<AttachmentParameter> getAttachmentParameterIterator() {
        return attachmentList.values().iterator();
    }

    /**
     * Adds a given {@link AttachmentParameter} to this QualityAssessment object.
     *
     * If a AttachmentParameter with the same accession number was already present, the old AttachmentParameter is replaced by the given AttachmentParameter.
     *
     * @param param  The given AttachmentParameter
     */
    public void addAttachmentParameter(AttachmentParameter param) {
        if(param != null) {
            // add the bi-directional relationship
            param.setParentQualityAssessment(this);
            attachmentList.put(param.getAccession(), param);
        } else {
            LOGGER.error("Can't add <null> AttachmentParameter to a QualityAssessment object");
            throw new NullPointerException("Can't add <null> AttachmentParameter");
        }
    }

    /**
     * Removes the {@link AttachmentParameter} contained in this QualityAssessment object, specified by the given accession number, from this QualityAssessment object.
     *
     * @param accession  The accession number of the AttachmentParameter that will be removed
     */
    public void removeAttachmentParameter(String accession) {
        if(accession != null) {
            AttachmentParameter param = getAttachmentParameter(accession);
            if(param != null) {
                // remove the bi-directional relationship
                param.setParentQualityAssessment(null);
            }
            attachmentList.remove(accession);
        }
    }

    /**
     * Removes all {@link AttachmentParameter}s contained in this QualityAssessment object, from this QualityAssessment object.
     */
    public void removeAllAttachmentParameters() {
        Iterator<AttachmentParameter> it = getAttachmentParameterIterator();
        while(it.hasNext()) {
            // first remove the bi-directional relationship
            AttachmentParameter param = it.next();
            param.setParentQualityAssessment(null);
            // remove the AttachmentParameter
            it.remove();
        }
    }

    /**
     * Returns the flag indicating whether this is a runQuality or a setQuality.
     *
     * @return The flag indicating whether this is a runQuality or a setQuality
     */
    public boolean isSet() {
        return isSet;
    }

    /**
     * Sets the flag indicating whether this is a runQuality or a setQuality.
     *
     * @param isSet  The flag indicating whether this is a runQuality or a setQuality
     */
    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }

    /**
     * Returns the parent {@link QcML} object in which this QualityAssessment object is enclosed.
     *
     * @return The parent QcML object
     */
    public QcML getParentQcML() {
        return parentQcML;
    }

    /**
     * Sets the parent {@link QcML} object in which this QualityAssessment object is enclosed.
     *
     * Make sure that when setting this relationship, this QualityAssessment object effectively is added as a runQuality or a setQuality to the parent QcML object.
     *
     * @param parent  The parent QcML object
     */
    public void setParentQcML(QcML parent) {
        this.parentQcML = parent;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this).add("id", id).add("set", isSet);
        for(Iterator<MetaDataParameter> it = getMetaDataParameterIterator(); it.hasNext(); ) {
            tsh.add("metadata", it.next());
        }
        for(Iterator<QualityParameter> it = getQualityParameterIterator(); it.hasNext(); ) {
            tsh.add("quality", it.next());
        }
        for(Iterator<AttachmentParameter> it = getAttachmentParameterIterator(); it.hasNext(); ) {
            tsh.add("attachment", it.next());
        }
        return tsh.toString();
    }

}
