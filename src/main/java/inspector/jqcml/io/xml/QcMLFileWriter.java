package inspector.jqcml.io.xml;

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;

import java.io.File;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * A qcML output writer which writes to an XML-based qcML file.
 */
public class QcMLFileWriter implements QcMLWriter {
	
	private static final Logger logger = LogManager.getLogger(QcMLFileWriter.class);

    /** A {@link javax.xml.validation.Schema} representing the qcML XML schema, which can be used to validate a qcML file */
    private static final Schema schema = createSchema();

    /** The marshaller used to write the current qcML file through JAXB */
	private QcMLMarshaller marshaller;

    /**
     * Creates a {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file.
     *
     * @return A Schema representing the qcML XML schema
     */
    private static Schema createSchema() {
        URL schemaUrl = QcMLFileWriter.class.getResource("/qcML_0.0.8.xsd");
        try {
            logger.info("Create schema validator from xsd file <{}>", schemaUrl.getFile());

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(schemaUrl);

        } catch (SAXException e) {
            logger.error("File <{}> is no valid XML schema: ", schemaUrl.getFile(), e.getMessage());
            throw new IllegalArgumentException("No valid XML schema: " + schemaUrl.getFile());
        }
    }

	/**
	 * Creates a QcMLWriter by initializing a {@link QcMLMarshaller}.
	 */
	public QcMLFileWriter() {
		marshaller = new QcMLMarshaller(schema);
	}
	
	/**
	 * Sets the file to which the Writer will write.
	 * 
	 * @param fileName  The file name of the file to which the Writer will write
	 */
	private File setFile(String fileName) {
		// check whether the file name is valid
		if(fileName == null) {
			logger.error("Invalid file name <null>");
			throw new NullPointerException("Invalid file name");
		}
				
		File qcmlFile = new File(fileName);
		logger.info("Set qcML file <{}>", qcmlFile.getAbsoluteFile());
		
		return qcmlFile;
	}
	
	@Override
	public void writeQcML(QcML qcml) {
		if(qcml != null) {
			// check if the version corresponds to the XML schema version
			if(!qcml.getVersion().equals(QCML_VERSION)) {
				// if the version was incorrect, issue a warning that it was changed
				if(qcml.getVersion() != null)
					logger.warn("qcML version number changed to <{}>", QCML_VERSION);
				qcml.setVersion(QCML_VERSION);
			}

			File file = setFile(qcml.getFileName());
			marshaller.marshal(qcml, file);
		}
		else {
			logger.error("Unable to marshal <null> qcML element to file");
			throw new NullPointerException("Unable to marshal <null> qcML element to file");
		}
	}

	
	/**
	 * Unsupported operation for a QcMLFileWriter. Only possible for a QcDBWriter.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void writeCv(Cv cv) {
		throw new UnsupportedOperationException("Invalid operation for XML-based qcML files (try this on a qcDB RDBMS instead).");
	}

}
