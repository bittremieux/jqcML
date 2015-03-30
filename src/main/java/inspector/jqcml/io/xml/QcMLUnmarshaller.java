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

import inspector.jqcml.io.xml.index.QcMLIndexer;
import inspector.jqcml.jaxb.NamespaceFilter;
import inspector.jqcml.jaxb.listener.QcMLListener;
import inspector.jqcml.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

/**
 * A JAXB {@link Unmarshaller} specified with the required context to deserialize an XML-based qcML file into Java objects.
 */
public class QcMLUnmarshaller {

    private static final Logger LOGGER = LogManager.getLogger(QcMLUnmarshaller.class);

    /** The unmarshaller used to deserialize XML files */
    private Unmarshaller unmarshaller;
    /** Introspector used to retrieve additional details during unmarshalling */
    private JAXBIntrospector introspector;
    /** Filter to handle the qcML namespace when unmarshalling only a section of a qcML file */
    private NamespaceFilter namespaceFilter;
    /** Schema used to validate during unmarshalling */
    private Schema schema;

    /**
     * Creates a JAXB {@link Unmarshaller} using the required {@link JAXBContext} required for unmarshalling a qcML file.
     */
    public QcMLUnmarshaller() {
        // create the JAXB Unmarshaller
        LOGGER.info("Create the JAXB Unmarshaller");

        try {
            unmarshaller = QcMLJAXBContext.INSTANCE.context.createUnmarshaller();
            // register the listener
            unmarshaller.setListener(new QcMLListener());
            introspector = QcMLJAXBContext.INSTANCE.context.createJAXBIntrospector();
            // create a filter to deal with (missing) namespaces
            namespaceFilter = createNamespaceFilter();

        } catch (JAXBException e) {
            LOGGER.error("Error while creating the JAXB Unmarshaller: {}", e);
            throw new IllegalStateException("Could not create the qcML Unmarshaller: " + e);
        }
    }

    /**
     * Creates a JAXB {@link Unmarshaller} validated by the provided {@link Schema},
     * using the required {@link JAXBContext} required for unmarshalling a qcML file.
     *
     * @param schema  the {@link Schema} used to perform validation during unmarshalling
     */
    public QcMLUnmarshaller(Schema schema) {
        this();

        this.schema = schema;
        unmarshaller.setSchema(schema);
    }

    /**
     * Creates a {@link NamespaceFilter} used during unmarshalling.
     *
     * This filter injects the qcML namespace when not the full XML-document is being unmarshalled.
     *
     * @return A NamespaceFilter to handle the qcML namespace
     */
    private NamespaceFilter createNamespaceFilter() {
        try {
            // create an XMLReader to use with the namespace filter
            XMLReader reader = XMLReaderFactory.createXMLReader();

            // create the namespace filter and set the XMLReader as its parent.
            NamespaceFilter nsFilter = new NamespaceFilter("http://www.prime-xs.eu/ms/qcml", true);
            nsFilter.setParent(reader);

            return nsFilter;

        } catch (SAXException e) {
            LOGGER.error("Could not create a default XML reader for unmarshalling: {}", e);
            throw new IllegalStateException("Could not create a default XML reader for unmarshalling: " + e);
        }
    }

    public JAXBIntrospector getIntrospector() {
        return introspector;
    }

    /**
     * Returns the full {@link QcML} object specified by the given file.
     *
     * Warning: Because a single QcML object can contain several QualityAssessments,
     * only use this method when you are certain the requested QcML object won't be too big,
     * in order to prevent running out of main memory.
     *
     * @param file  The file that will be unmarshalled. This should be a valid file.
     * @return The {@link QcML} object unmarshalled from the given file
     */
    public QcML unmarshal(File file) {
        LOGGER.info("Unmarshal full file <{}>", file.getAbsolutePath());

        try {
            QcML result = (QcML) unmarshaller.unmarshal(createSource(file));
            result.setFileName(file.getName());

            return result;

        } catch(FileNotFoundException e) {
            LOGGER.error("The specified file to unmarshal doesn't exist: <{}>", file.getAbsolutePath(), e);
            throw new IllegalStateException("The specified file to unmarshal doesn't exist <" + file.getAbsolutePath() + ">");
        } catch(JAXBException e) {
            LOGGER.error("Error while unmarshalling file <{}>: {}", file.getAbsolutePath(), e);
            throw new IllegalStateException("Error while unmarshalling file <" + file.getAbsolutePath() + ">: " + e);
        }
    }

    /**
     * Returns the specified object from the given XML snippet.
     *
     * @param <T>  The class of the object unmarshalled from the given XML snippet
     * @param xmlSnippet  The XML snippet that will be unmarshalled. This should be valid XML content.
     * @param type  The class of the object unmarshalled from the given XML snippet
     * @return The object unmarshalled from the given XML snippet
     */
    public <T> T unmarshal(String xmlSnippet, Class<T> type) {
        LOGGER.info("Unmarshal type <{}> from XML snippet: {}", type, xmlSnippet.substring(0, xmlSnippet.indexOf('>') + 1));

        try {
            // disable validation
            unmarshaller.setSchema(null);

            // unmarshal
            Object temp = unmarshaller.unmarshal(createSource(xmlSnippet));

            return type.cast(JAXBIntrospector.getValue(temp));

        } catch (JAXBException e) {
            LOGGER.error("Error while unmarshalling XML snippet {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>') + 1), e);
            throw new IllegalStateException("Error while unmarshalling XML snippet " + xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1) + ": " + e);
        } finally {
            // re-enable validation
            unmarshaller.setSchema(schema);
        }
    }

    /**
     * Returns the specified object from the given XML snippet.
     *
     * The return value is a JAXB element. This allows f.e. retrieving the element name and value using the {@link JAXBIntrospector}.
     *
     * @param xmlSnippet  The XML snippet that will be unmarshalled. This should be valid XML content.
     * @return The object unmarshalled from the given XML snippet
     */
    public Object unmarshal(String xmlSnippet) {
        LOGGER.info("Unmarshal general object from XML snippet: {}", xmlSnippet.substring(0, xmlSnippet.indexOf('>') + 1));

        try {
            // disable validation
            unmarshaller.setSchema(null);

            // unmarshal
            return unmarshaller.unmarshal(createSource(xmlSnippet));

        } catch (JAXBException e) {
            LOGGER.error("Error while unmarshalling XML snippet {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>') + 1), e);
            throw new IllegalStateException("Error while unmarshalling XML snippet " + xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1) + ": " + e);
        } finally {
            // re-enable validation
            unmarshaller.setSchema(schema);
        }
    }

    /**
     * Converts the given file to a {@link SAXSource}.
     *
     * @param file  The file to convert
     * @return A SAXSource representing the given file
     * @throws FileNotFoundException
     */
    private SAXSource createSource(File file) throws FileNotFoundException {
        // convert the input to an InputSource
        InputSource is = new InputSource(new FileInputStream(file));

        // create a SAXSource specifying the namespace filter
        return new SAXSource(namespaceFilter, is);
    }

    /**
     * Converts the given XML snippet to a {@link SAXSource}.
     *
     * @param xmlSnippet  The XML snippet to convert
     * @return A SAXSource representing the given XML snippet
     */
    private SAXSource createSource(String xmlSnippet) {
        // convert the input to an InputSource
        InputSource is = new InputSource(new StringReader(xmlSnippet));

        // create a SAXSource specifying the namespace filter
        return new SAXSource(namespaceFilter, is);
    }

    /**
     * Resolves references to a {@link Cv} for all parameters in the given {@link QualityAssessment}.
     *
     * If the referenced Cv has already been unmarshalled in the past and is available in the Cv cache, it isn't unmarshalled again.
     * If the referenced Cv isn't available in the cache, it is unmarshalled using the {@link QcMLUnmarshaller}.
     *
     * @param qa  the QualityAssessment containing the parameters for which the references will be resolved
     * @param cvCache  a cache containing the previously unmarshalled Cv's
     * @param index  the {@link QcMLIndexer} used to unmarshal
     */
    public void resolveCvReferences(QualityAssessment qa, Map<String, Cv> cvCache, QcMLIndexer index) {
        // for each QualityParameter
        for(Iterator<QualityParameter> it = qa.getQualityParameterIterator(); it.hasNext(); ) {
            resolveCvReferences(it.next(), cvCache, index);
        }
        // for each AttachmentParameter
        for(Iterator<AttachmentParameter> it = qa.getAttachmentParameterIterator(); it.hasNext(); ) {
            resolveCvReferences(it.next(), cvCache, index);
        }
    }

    private void resolveCvReferences(CvParameter param, Map<String, Cv> cvCache, QcMLIndexer index) {
        // cvRef
        String cvId = param.getCvRefId();
        Cv cvRef = resolveCvReference(cvId, cvCache, index);
        param.setCvRef(cvRef);

        // unitCvRef
        String unitCvId = param.getUnitCvRefId();
        Cv unitCvRef = resolveCvReference(unitCvId, cvCache, index);
        param.setUnitCvRef(unitCvRef);
    }

    private Cv resolveCvReference(String id, Map<String, Cv> cvCache, QcMLIndexer index) {
        // Cv already unmarshalled and found in cache
        if(cvCache.containsKey(id)) {
            return cvCache.get(id);
        } else {
            // unmarshal the Cv
            String xmlSnippet = index.getXMLSnippet(Cv.class, id);
            // can be null if the Cv isn't found in the file
            if(xmlSnippet != null) {
                Cv cv = unmarshal(xmlSnippet, Cv.class);
                // if this is a viable Cv, store it in the cache
                if(cv != null) {
                    cvCache.put(id, cv);
                    return cv;
                }
            }
        }
        return null;
    }
}
