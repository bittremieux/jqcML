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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import psidev.psi.tools.xxindex.index.IndexElement;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Iterator;

/**
 * An {@link Iterator} over a specified object in a qcML file.
 * 
 * After unmarshalling the object from the qcML file, an {@link XmlAdapter} is used to transform the object.
 * 
 * @param <S> The qcML (sub)object that is unmarshalled from the qcML file.
 *           If this subobject is included in the {@link QcMLIndexer}, the iterator will run over all indexed items.
 *           Else, the iterator will be empty.
 * @param <T> The qcML (sub)object over which the iterator runs.
 *           This object type is obtained by transforming the unmarshalled object using an {@link XmlAdapter}.
 */
public class QcMLAdapterIterator<S, T> extends QcMLIterator<T> {

    private static final Logger LOGGER = LogManager.getLogger(QcMLAdapterIterator.class);

    /** The intermediate class type of the iterator */
    protected Class<S> preClass;
    /** The adapter to convert between the intermediate class type and the resulting class type */
    protected XmlAdapter<S, T> adapter;

    /**
     * Create a QcMLAdapterIterator that will iterate over qcML (sub)objects of the specified class type.
     *
     * @param index  The {@link QcMLIndexer} used to index the qcML file
     * @param unmarshaller  The {@link QcMLUnmarshaller} to unmarshal the qcML file
     * @param preClass  The class type of the unmarshalled object prior to conversion with the adapter
     * @param postClass  The class type of the unmarshalled object after the conversion with the adapter
     * @param adapter  The {@link XmlAdapter} used to convert the unmarshalled object
     */
    public QcMLAdapterIterator(QcMLIndexer index, QcMLUnmarshaller unmarshaller, Class<S> preClass, Class<T> postClass, XmlAdapter<S, T> adapter) {
        super(index, unmarshaller, postClass);

        this.preClass = preClass;
        this.adapter = adapter;
    }

    @Override
    public T next() {
        LOGGER.info("Retrieve next element from the iterator for type <{}>", clss);

        // get the next index element
        IndexElement elem = iterator.next().getValue();
        // read the xml snippet
        String xmlSnippet = index.readXML(elem);
        // unmarshal this element
        S temp = unmarshaller.unmarshal(xmlSnippet, preClass);
        // manually call the adapter
        try {
            return adapter.unmarshal(temp);
        } catch (Exception e) {
            LOGGER.error("Unable to manually call adapter {} for XML snippet: {}\n{}", adapter, xmlSnippet.substring(0, xmlSnippet.indexOf('>')+1), e);
            throw new IllegalStateException("Unable to manually call the XmlAdapter: " + e);
        }
    }

}
