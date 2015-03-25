package inspector.jqcml.io.xml;

import inspector.jqcml.model.QcML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.StringWriter;

/**
 * A JAXB {@link Marshaller} specified with the required context to serialize Java objects to an XML-based qcML file.
 */
public class QcMLMarshaller {

    private static final Logger LOGGER = LogManager.getLogger(QcMLMarshaller.class);

    /** Marshaller used to serialize Java objects to a qcML file */
    private Marshaller marshaller;

    /**
     * Creates a JAXB {@link Marshaller} using the required {@link JAXBContext} required for marshalling a qcML file.
     */
    public QcMLMarshaller() {
        // create the JAXB Marshaller
        LOGGER.info("Create the JAXB Marshaller");

        try {
            marshaller = QcMLJAXBContext.INSTANCE.context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            LOGGER.error("Error while creating the JAXB Marshaller: {}", e);
            throw new IllegalStateException("Could not create the qcML Marshaller: " + e);
        }
    }

    /**
     * Creates a JAXB {@link Marshaller} validated by the provided {@link javax.xml.validation.Schema},
     * using the required {@link JAXBContext} required for marshalling a qcML file.
     *
     * @param schema  the {@link Schema} used to perform validation during marshalling
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
        LOGGER.info("Marshal to string");

        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(qcml, sw);

            return sw.toString();
        } catch (JAXBException e) {
            LOGGER.error("Error while marshalling to string: {}", e);
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
        LOGGER.info("Marshal to file <{}>", file.getAbsolutePath());

        try {
            marshaller.marshal(qcml, file);
        } catch (JAXBException e) {
            LOGGER.error("Error while marshalling file <{}>: {}", file.getAbsolutePath(), e);
            throw new IllegalStateException("Error while marshalling file <" + file.getAbsolutePath() + ">: " + e);
        }
    }

}
