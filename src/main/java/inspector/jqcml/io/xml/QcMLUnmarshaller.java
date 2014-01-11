package inspector.jqcml.io.xml;

import inspector.jqcml.jaxb.NamespaceFilter;
import inspector.jqcml.jaxb.listener.QcMLListener;
import inspector.jqcml.model.QcML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A JAXB {@link Unmarshaller} specified with the required context to deserialize an XML-based qcML file into Java objects.
 */
public class QcMLUnmarshaller {

	private static final Logger logger = LogManager.getLogger(QcMLUnmarshaller.class);

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
		logger.info("Create the JAXB Unmarshaller");
		
		try {
			unmarshaller = QcMLJAXBContext.INSTANCE.context.createUnmarshaller();
			// register the listener
			unmarshaller.setListener(new QcMLListener());
			introspector = QcMLJAXBContext.INSTANCE.context.createJAXBIntrospector();
			// create a filter to deal with (missing) namespaces
			namespaceFilter = createNamespaceFilter();
			
		} catch (JAXBException e) {
			logger.error("Error while creating the JAXB Unmarshaller: {}", e);
			throw new IllegalStateException("Could not create the qcML Unmarshaller: " + e);
		}
	}

    /**
     * Creates a JAXB {@link Unmarshaller} validated by the provided {@link Schema},
     * using the required {@link JAXBContext} required for unmarshalling a qcML file.
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
			logger.error("Could not create a default XML reader for unmarshalling: {}", e);
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
		logger.info("Unmarshal full file <{}>", file.getAbsolutePath());
		
		try {
			QcML result = (QcML) unmarshaller.unmarshal(createSource(file));
			result.setFileName(file.getName());

			return result;
			
		} catch (FileNotFoundException e) {
			logger.error("The specified file to unmarshal doesn't exist: <{}>", file.getAbsolutePath());
			throw new IllegalStateException("The specified file to unmarshal doesn't exist <" + file.getAbsolutePath() + ">");
		} catch (JAXBException e) {
			logger.error("Error while unmarshalling file <{}>: {}", file.getAbsolutePath(), e);
			throw new IllegalStateException("Error while unmarshalling file <" + file.getAbsolutePath() + ">: " + e);
		}
	}
	
	/**
	 * Returns the specified object from the given XML snippet.
	 * 
	 * @param xmlSnippet  The XML snippet that will be unmarshalled. This should be valid XML content.
	 * @param type  The class of the object unmarshalled from the given XML snippet
	 * @return The object unmarshalled from the given XML snippet
	 */
	public <T> T unmarshal(String xmlSnippet, Class<T> type) {
		logger.info("Unmarshal type <{}> from XML snippet: {}", type, xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1));
		
		try {
            // disable validation
            unmarshaller.setSchema(null);

            // unmarshal
			Object temp = unmarshaller.unmarshal(createSource(xmlSnippet));

            return type.cast(JAXBIntrospector.getValue(temp));
			
		} catch (JAXBException e) {
			logger.error("Error while unmarshalling XML snippet {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1), e);
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
		logger.info("Unmarshal general object from XML snippet: {}", xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1));
		
		try {
            // disable validation
            unmarshaller.setSchema(null);

            // unmarshal
			return unmarshaller.unmarshal(createSource(xmlSnippet));
			
		} catch (JAXBException e) {
			logger.error("Error while unmarshalling XML snippet {}\n{}", xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1), e);
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

}
