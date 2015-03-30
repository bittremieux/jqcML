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

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;

/**
 * A qcML output writer which writes to an XML-based qcML file.
 */
public class QcMLFileWriter implements QcMLWriter {

    private static final Logger LOGGER = LogManager.getLogger(QcMLFileWriter.class);

    /** A {@link javax.xml.validation.Schema} representing the qcML XML schema, which can be used to validate a qcML file */
    private static final Schema SCHEMA = createSchema();

    /** The marshaller used to write the current qcML file through JAXB */
    private QcMLMarshaller marshaller;

    /**
     * Creates a QcMLWriter by initializing a {@link QcMLMarshaller}.
     */
    public QcMLFileWriter() {
        marshaller = new QcMLMarshaller(SCHEMA);
    }

    /**
     * Creates a {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file.
     *
     * @return A Schema representing the qcML XML schema
     */
    private static Schema createSchema() {
        URL schemaUrl = QcMLFileWriter.class.getResource("/qcML_0.0.8.xsd");
        try {
            LOGGER.info("Create schema validator from xsd file <{}>", schemaUrl.getFile());

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(schemaUrl);

        } catch(SAXException e) {
            LOGGER.error("File <{}> is no valid XML schema", schemaUrl.getFile(), e);
            throw new IllegalArgumentException("No valid XML schema: " + schemaUrl.getFile());
        }
    }

    /**
     * Sets the file to which the Writer will write.
     *
     * @param fileName  The file name of the file to which the Writer will write
     */
    private File setFile(String fileName) {
        // check whether the file name is valid
        if(fileName == null) {
            LOGGER.error("Invalid file name <null>");
            throw new NullPointerException("Invalid file name");
        }

        File qcmlFile = new File(fileName);
        LOGGER.info("Set qcML file <{}>", qcmlFile.getAbsoluteFile());

        return qcmlFile;
    }

    @Override
    public void writeQcML(QcML qcml) {
        if(qcml != null) {
            // check if the version corresponds to the XML schema version
            if(qcml.getVersion() == null || !qcml.getVersion().equals(QCML_VERSION)) {
                // if the version was incorrect, issue a warning that it was changed
                if(qcml.getVersion() != null) {
                    LOGGER.warn("qcML version number changed to <{}>", QCML_VERSION);
                }
                qcml.setVersion(QCML_VERSION);
            }

            File file = setFile(qcml.getFileName());
            marshaller.marshal(qcml, file);
        } else {
            LOGGER.error("Unable to marshal <null> qcML element to file");
            throw new NullPointerException("Unable to marshal <null> qcML element to file");
        }
    }


    /**
     * Unsupported operation for a QcMLFileWriter. Only possible for a QcDBWriter.
     *
     * @throws UnsupportedOperationException  this operation is unsupported for XML-based qcML files
     */
    @Override
    public void writeCv(Cv cv) {
        throw new UnsupportedOperationException("Invalid operation for XML-based qcML files (try this on a qcDB RDBMS instead).");
    }

}
