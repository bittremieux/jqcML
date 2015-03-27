package inspector.jqcml.io.xml;

import inspector.jqcml.io.xml.index.QcMLIndexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import psidev.psi.tools.xxindex.index.IndexElement;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * An {@link Iterator} over a specified object in a qcML file.
 * 
 * @param <T> The qcML (sub)object over which the iterator runs.
 *           If this subobject is included in the {@link QcMLIndexer}, the iterator will run over all indexed items.
 *           Else, the iterator will be empty.
 */
public class QcMLIterator<T> implements Iterator<T> {

    private static final Logger LOGGER = LogManager.getLogger(QcMLIterator.class);

    /** The resulting class type of the iterator */
    protected Class<T> clss;
    /** The index for the current qcML file, recording the offsets of the individual elements */
    protected QcMLIndexer index;
    /** The unmarshaller used to read the qcML file through JAXB */
    protected QcMLUnmarshaller unmarshaller;

    /** Iterator running over all entries of the specified class in the index */
    protected Iterator<Entry<String, IndexElement>> iterator;

    /**
     * Creates a QcMLIterator that will iterate over qcML (sub)objects of the specified class type.
     *
     * @param index  The {@link QcMLIndexer} used to index the qcML file
     * @param unmarshaller  The {@link QcMLUnmarshaller} to unmarshal the qcML file
     * @param clss  The class type of the object returned by the iterator
     */
    public QcMLIterator(QcMLIndexer index, QcMLUnmarshaller unmarshaller, Class<T> clss) {
        this.clss = clss;
        this.index = index;
        this.unmarshaller = unmarshaller;

        // check whether the given class type has some mappings in the index
        if(index.getIDMapping(clss) != null) {
            iterator = index.getIDMapping(clss).entrySet().iterator();
        } else {
            // no mappings found for the given element -> empty iterator
            iterator = Collections.emptyIterator();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        LOGGER.info("Retrieve next element from the iterator for type <{}>", clss);

        // get the next index element
        IndexElement elem = iterator.next().getValue();
        // read the xml snippet
        String xmlSnippet = index.readXML(elem);
        // unmarshal this element
        return unmarshaller.unmarshal(xmlSnippet, clss);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Unable to remove objects while iterating");
    }

}
