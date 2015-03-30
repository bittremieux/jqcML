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

import inspector.jqcml.jpa.listener.QcDBParameterListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
    protected CvParameter() {
        super();
    }

    /**
     * Constructs a new CvParameter object with the given name and defined by the given {@link Cv} object.
     *
     * @param name  The name of the parameter, not {@code null}
     * @param cvRef  The reference to the Cv object which defines this parameter, not {@code null}
     * @param accession  The accession number identifying this parameter in the controlled vocabulary, not {@code null}
     */
    public CvParameter(String name, Cv cvRef, String accession) {
        setName(name);
        setCvRef(cvRef);
        setAccession(accession);
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
     * @param accession  The accession number identifying this parameter in the controlled vocabulary, not {@code null}
     */
    protected void setAccession(String accession) {
        if(accession != null) {
            this.accession = accession;
        } else {
            LOGGER.error("The parameter's accession is not allowed to be <null>");
            throw new NullPointerException("The parameter's accession is not allowed to be <null>");
        }
    }
}
