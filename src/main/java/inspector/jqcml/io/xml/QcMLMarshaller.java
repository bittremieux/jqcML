package inspector.jqcml.io.xml;

import java.io.File;
import java.io.StringWriter;

import inspector.jqcml.model.QcML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A JAXB {@link Marshaller} specified with the required context to serialize Java objects to an XML-based qcML file.
 */
public class QcMLMarshaller {
		
	private static final Logger logger = LogManager.getLogger(QcMLMarshaller.class);

    /** Marshaller used to serialize Java objects to a qcML file */
	private Marshaller marshaller;
	
	/**
	 * Creates a JAXB {@link Marshaller} using the required {@link JAXBContext} required for marshalling a qcML file.
	 */
	public QcMLMarshaller() {
		// create the JAXB Marshaller
		logger.info("Create the JAXB Marshaller");
		
		try {
			marshaller = QcMLJAXBContext.INSTANCE.context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			logger.error("Error while creating the JAXB Marshaller: {}", e);
			throw new IllegalStateException("Could not create the qcML Marshaller: " + e);
		}
	}

    /**
     * Creates a JAXB {@link Marshaller} validated by the provided {@link javax.xml.validation.Schema},
     * using the required {@link JAXBContext} required for marshalling a qcML file.
     */
    public QcMLMarshaller(Schema schema) {
        this();

        marshaller.setSchema(schema);
    }

	/**
	 * Marshals a {@link QcML} object to a String.
	 * 
	 * @param qcml  The {@link QcML} object that will be marshalled. This should be a valid QcML object.
	 * @return The {@link QcML} object serialized to XML format as a String
	 */
	public String marshal(QcML qcml) {
		logger.info("Marshal to string");
		
		try {
			StringWriter sw = new StringWriter();
			marshaller.marshal(qcml, sw);
			
			return sw.toString();
		} catch (JAXBException e) {
			logger.error("Error while marshalling to string: {}", e);
			throw new IllegalStateException("Error while marshalling to string: " + e);
		}
	}
	
	/**
	 * Marshals a {@link QcML} object to the specified file.
	 * 
	 * @param qcml  The {@link QcML} object that will be marshalled. This should be a valid QcML object.
	 * @param file  The file to which the {@link QcML} object will be serialized. This should be a valid file.
	 */
	public void marshal(QcML qcml, File file) {
		logger.info("Marshal to file <{}>", file.getAbsolutePath());
		
		try {
			marshaller.marshal(qcml, file);			
		} catch (JAXBException e) {
			logger.error("Error while marshalling file <{}>: {}", file.getAbsolutePath(), e);
			throw new IllegalStateException("Error while marshalling file <" + file.getAbsolutePath() + ">: " + e);
		}
	}

}
