/**
 * 
 */
package inspector.jqcml.io.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import inspector.jqcml.io.QcMLReader;
import inspector.jqcml.io.xml.index.QcMLIndexer;
import inspector.jqcml.jaxb.adapters.QualityAssessmentAdapter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityAssessmentList;

/**
 * A qcML input reader which takes its input from an XML-based qcML file.
 */
public class QcMLFileReader implements QcMLReader {
	
	private static final Logger logger = LogManager.getLogger(QcMLFileReader.class);

    /** A {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file */
	private static final Schema schema = createSchema();

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
     * Creates a {@link Schema} representing the qcML XML schema, which can be used to validate a qcML file.
     *
     * @return A Schema representing the qcML XML schema
     */
	private static Schema createSchema() {
		URL schemaUrl = QcMLFileReader.class.getResource("/qcML_0.0.7.xsd");
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
	 * Creates a QcMLFileReader by initializing a {@link QcMLUnmarshaller}.
	 */
	public QcMLFileReader() {
		unmarshaller = new QcMLUnmarshaller(schema);
	}
	
	/**
	 * Sets the file from which the Reader will read, and creates an index.
	 * 
	 * @param fileName  The file name of the qcML file from which the Reader will read
	 */
	private void setFile(String fileName) {		
		// check whether the file name is valid
		if(fileName == null) {
			logger.error("Invalid file name <null>");
			throw new NullPointerException("Invalid file name");
		}
		
		File file = new File(fileName);

		// verify whether the same file was previously checked
		// in that case, assume all checks have been done and the index can be reused
		if(!file.equals(currentFile)) {
			// check whether the file exists
			if(!file.exists()) {
				logger.error("The qcML file <{}> does not exist", file.getAbsolutePath());
				throw new IllegalArgumentException("The qcML file to read does not exist: " + file.getAbsolutePath());
			}

			currentFile = file;
			logger.info("Read from qcML file <{}>", currentFile.getAbsoluteFile());
			
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
            schema.newValidator().validate(new StreamSource(currentFile));

            // validated successfully
            return true;

        } catch (SAXException e) {
            logger.error("File <{}> does not contain valid qcML content: {}", currentFile.getAbsolutePath(), e.getMessage());

            // validated unsuccessfully
            return false;

        } catch (IOException e) {
            logger.error("The qcML file <{}> could not be read for validation", currentFile.getAbsolutePath());
            throw new IllegalArgumentException("The qcML file could not be read for validation: " + currentFile.getAbsolutePath());
        }
    }

	@Override
	public QcML getQcML(String qcmlFile) {
		try {
			setFile(qcmlFile);
			return unmarshaller.unmarshal(currentFile);
		} catch(IllegalStateException e) {
			return null;
		}
	}

    @Override
	public Cv getCv(String qcmlFile, String id) {
		setFile(qcmlFile);
		
		// retrieve the XML snippet pertaining to this CVType
		String xmlSnippet = index.getXMLSnippet(Cv.class, id);
		
		// unmarshal the XML snippet
		if(xmlSnippet != null)
			return unmarshaller.unmarshal(xmlSnippet, Cv.class);
		else
			return null;
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
				HashMap<String, Cv> cvCache = new HashMap<>();
				adapter.resolveReferences(result, cvCache, index, unmarshaller);
				
				// set the isSet flag based on the element name
				if(unmarshaller.getIntrospector().getElementName(temp).getLocalPart().equals("setQuality"))
					result.setSet(true);
				else
					result.setSet(false);
				
				return result;
			} catch (Exception e) {
				logger.error("Unable to manually call the QualityAssessmentAdapter for XML snippet: {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1), e);
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
