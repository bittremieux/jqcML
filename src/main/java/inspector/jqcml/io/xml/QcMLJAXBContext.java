package inspector.jqcml.io.xml;

import inspector.jqcml.model.QcML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * JAXBContext singleton.
 * Because JAXBContext is thread-safe, it is encapsulated in a singleton so it will only be created once and be reused to avoid the cost of initializing the metadata multiple times.
 * 
 * See: http://jaxb.java.net/guide/Performance_and_thread_safety.html
 */
public enum QcMLJAXBContext {

    INSTANCE;

    private final Logger LOGGER = LogManager.getLogger(QcMLJAXBContext.class);

    public final JAXBContext context = initContext();

    /**
     * Because JAXBContext is thread-safe, this should only be created once and reused to avoid the cost of initializing the metadata multiple times.
     * Hence the encapsulation in a Singleton object.
     *
     * @return The JAXB Context for (un)marshalling qcML files.
     */
    private JAXBContext initContext() {
        try {
            LOGGER.info("Create the JAXB Context");
            return JAXBContext.newInstance(QcML.class);
        } catch (JAXBException e) {
            LOGGER.error("Error while creating the JAXB Context: {}", e);
            throw new IllegalStateException("Could not create the JAXB Context: " + e);
        }
    }

}
