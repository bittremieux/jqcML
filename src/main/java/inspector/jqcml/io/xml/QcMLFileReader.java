/**
 * 
 */
package inspector.jqcml.io.xml;

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

import inspector.jqcml.io.QcMLReader;
import inspector.jqcml.io.xml.index.QcMLIndexer;
import inspector.jqcml.jaxb.adapters.QualityAssessmentAdapter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityAssessmentList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A qcML input reader which takes its input from an XML-based qcML file.
 */
public class QcMLFileReader implements QcMLReader {

    private static final Logger LOGGER = LogManager.getLogger(QcMLFileReader.class);

    /** A {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file */
    private static final Schema SCHEMA = createSchema();

    /**
     * The current qcML file from which we are reading.
     * Used to prevent having to create the index over again when performing several reads from the same file.
     */
    private File currentFile;

    /** An indexer used to record the offset of the elements within the current qcML file */
    private QcMLIndexer index;
    /** The unmarshaller used to read the current qcML file through JAXB */
    private QcMLUnmarshaller unmarshaller;

    /**
     * Creates a QcMLFileReader by initializing a {@link QcMLUnmarshaller}.
     */
    public QcMLFileReader() {
        unmarshaller = new QcMLUnmarshaller(SCHEMA);
    }

    /**
     * Creates a {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file.
     *
     * @return A Schema representing the qcML XML schema
     */
    private static Schema createSchema() {
        URL schemaUrl = QcMLFileReader.class.getResource("/qcML_0.0.8.xsd");
        try {
            LOGGER.info("Create schema validator from xsd file <{}>", schemaUrl.getFile());

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(schemaUrl);

        } catch (SAXException e) {
            LOGGER.error("File <{}> is no valid XML schema: ", schemaUrl.getFile(), e);
            throw new IllegalArgumentException("No valid XML schema: " + schemaUrl.getFile());
        }
    }

    /**
     * Sets the file from which the Reader will read, and creates an index.
     *
     * @param fileName  The file name of the qcML file from which the Reader will read
     */
    private void setFile(String fileName) {
        // check whether the file name is valid
        if(fileName == null) {
            LOGGER.error("Invalid file name <null>");
            throw new NullPointerException("Invalid file name");
        }

        File file = new File(fileName);

        // verify whether the same file was previously checked
        // in that case, assume all checks have been done and the index can be reused
        if(!file.equals(currentFile)) {
            // check whether the file exists
            if(!file.exists()) {
                LOGGER.error("The qcML file <{}> does not exist", file.getAbsolutePath());
                throw new IllegalArgumentException("The qcML file to read does not exist: " + file.getAbsolutePath());
            }

            currentFile = file;
            LOGGER.info("Read from qcML file <{}>", currentFile.getAbsoluteFile());

            // create the XML file index
            index = new QcMLIndexer(currentFile);
        }
    }

    /**
     * Validates the given file against the qcML XML schema.
     *
     * @param qcmlFile  The (qcML) file to be validated
     * @return True if the given file is a valid qcML file, false otherwise
     */
    public boolean validate(String qcmlFile) {
        try {
            setFile(qcmlFile);
            SCHEMA.newValidator().validate(new StreamSource(currentFile));

            // validated successfully
            return true;

        } catch(SAXException e) {
            LOGGER.error("File <{}> does not contain valid qcML content: ", currentFile.getAbsolutePath(), e);

            // validated unsuccessfully
            return false;

        } catch(IOException e) {
            LOGGER.error("The qcML file <{}> could not be read for validation", currentFile.getAbsolutePath(), e);
            throw new IllegalArgumentException("The qcML file could not be read for validation: " + currentFile.getAbsolutePath());
        }
    }

    @Override
    public QcML getQcML(String qcmlFile) {
        try {
            setFile(qcmlFile);
            QcML qcml = unmarshaller.unmarshal(currentFile);

            if(!qcml.getVersion().equals(QCML_VERSION)) {
                LOGGER.warn("The qcML version <{}> doesn't correspond to the qcML XML schema version <{}>", qcml.getVersion(), QCML_VERSION);
                qcml.setVersion(QCML_VERSION);
            }

            return qcml;

        } catch(IllegalStateException e) {
            LOGGER.info("Error while unmarshalling file <{}>", qcmlFile, e);
            return null;
        }
    }

    @Override
    public Cv getCv(String qcmlFile, String id) {
        setFile(qcmlFile);

        // retrieve the XML snippet pertaining to this CVType
        String xmlSnippet = index.getXMLSnippet(Cv.class, id);

        // unmarshal the XML snippet
        return xmlSnippet != null ? unmarshaller.unmarshal(xmlSnippet, Cv.class) : null;
    }

    @Override
    public Iterator<Cv> getCvIterator(String qcmlFile) {
        setFile(qcmlFile);

        return IteratorFactory.createCvIterator(index, unmarshaller);
    }

    @Override
    public QualityAssessment getQualityAssessment(String qcmlFile, String id) {
        setFile(qcmlFile);

        // retrieve the XML snippet
        String xmlSnippet = index.getXMLSnippet(QualityAssessment.class, id);

        // unmarshal the XML snippet if it exists
        if(xmlSnippet != null) {
            try {
                // unmarshal to a QualityAssessmentList, and subsequently call the QualityAssessmentAdapter manually
                // manually calling the adapter is required because XmlJavaTypeAdapter can't be used on XmlRootElement
                // see: https://java.net/jira/browse/JAXB-117
                Object temp = unmarshaller.unmarshal(xmlSnippet);
                QualityAssessmentList qaList = (QualityAssessmentList) JAXBIntrospector.getValue(temp);
                QualityAssessmentAdapter adapter = new QualityAssessmentAdapter();
                QualityAssessment result = adapter.unmarshal(qaList);

                // resolve references to Cv's (unmarshal them if required)
                // use a cache of unmarshalled Cv's because we might encounter the same Cv multiple times
                Map<String, Cv> cvCache = new HashMap<>();
                unmarshaller.resolveCvReferences(result, cvCache, index);

                // set the isSet flag based on the element name
                result.setSet("setQuality".equals(unmarshaller.getIntrospector().getElementName(temp).getLocalPart()));

                return result;
            } catch (Exception e) {
                LOGGER.error("Unable to manually call the QualityAssessmentAdapter for XML snippet: {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>') + 1), e);
                throw new IllegalStateException("Unable to manually call the QualityAssessmentAdapter: " + e);
            }
        }

        // no runQuality or setQuality with the specified ID found
        return null;
    }

    @Override
    public Iterator<QualityAssessment> getQualityAssessmentIterator(String qcmlFile) {
        setFile(qcmlFile);

        return IteratorFactory.createQualityAssessmentIterator(index, unmarshaller);
    }
}
