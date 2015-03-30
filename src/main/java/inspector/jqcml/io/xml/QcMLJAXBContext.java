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
