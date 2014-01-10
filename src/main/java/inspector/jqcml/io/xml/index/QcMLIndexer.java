package inspector.jqcml.io.xml.index;

import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QualityAssessment;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import psidev.psi.tools.xxindex.SimpleXmlElementExtractor;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.XmlElementExtractor;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XpathIndex;

/**
 * An index for an XML file, so the file can be used in a random-access fashion, retrieving only a specific XML element.
 *
 * This index records the offsets of each of the following XML elements in a qcML file:
 *  - runQuality
 *  - setQuality
 *  - cv
 */
public class QcMLIndexer {
	
	private static final Logger logger = LogManager.getLogger(QcMLIndexer.class);

    /** The qcML file that is indexed */
	private File qcmlFile;
	
	private StandardXpathAccess access;
	private XmlElementExtractor xmlExtractor;
	private XpathIndex index;

    /** Mapping between XML elements and their index in the qcML file */
	@SuppressWarnings("rawtypes")
	private Map<Class, Map<String, IndexElement>> idMap;

    /** Set containing the XPath node expressions of all elements that should be indexed */
	private static final Set<String> indexed_xpaths = getIndexedXPaths();

	/**
	 * Returns a set containing all XPath node expressions that need to be stored in the index.
	 * 
	 * These node expressions refer to the runQuality, setQuality ({@link QualityAssessment}) and {@link Cv} objects.
	 * 
	 * @return A set containing all XPath node expressions that need to be stored in the index
	 */
	private static Set<String> getIndexedXPaths() {
		Set<String> xpathsToIndex = new HashSet<>();

		// index all runQuality, setQuality and Cv
		xpathsToIndex.add("/qcML/runQuality");
		xpathsToIndex.add("/qcML/setQuality");
		xpathsToIndex.add("/qcML/cvList/cv");

		// finally make the set unmodifiable
		xpathsToIndex = Collections.unmodifiableSet(xpathsToIndex);

		return xpathsToIndex;
	}
	
	/**	A pattern for extracting an ID attribute from an XML element */
	private static final Pattern ID_PATTERN = Pattern.compile("\\sid\\s*=\\s*['\"]([^'\"]*)['\"]", Pattern.CASE_INSENSITIVE);

	/**
	 * Creates an XXIndex containing offsets for all runQualitys, setQualitys and Cv's.
	 * 
	 * @param file  The qcML file for which the index is created.
	 */
	@SuppressWarnings("rawtypes")
	public QcMLIndexer(File file) {
		try {
			logger.info("Create XXIndex");
			
			qcmlFile = file;

			// generate an XXIndex for the QualityAssessments (runQuality & setQuality) and Cv's
			access = new StandardXpathAccess(qcmlFile, indexed_xpaths);
			
			// create an XML element extractor
            xmlExtractor = new SimpleXmlElementExtractor();
            String encoding = xmlExtractor.detectFileEncoding(qcmlFile.toURI().toURL());
            if(encoding != null) {
            	logger.info("XML file encoding: {}", encoding);
                xmlExtractor.setEncoding(encoding);
            }
			
            // get the index
			index = access.getIndex();
			
			// generate ID mappings for all index elements
			idMap = new HashMap<>();
			createIDMappings();
			
		} catch (IOException e) {
			logger.error("Could not generate an index for qcML file <{}>: {}", qcmlFile.getAbsolutePath(), e);
			throw new IllegalStateException("Could not generate an index for qcML file: " + qcmlFile.getAbsolutePath());
		}
	}
	
	/**
	 * Returns the ID mappings for the specified class type.
	 * 
	 * @param clss  The class type for which the ID mappings are returned
	 * @return The ID mappings for the specified class type
	 */
	public Map<String, IndexElement> getIDMapping(@SuppressWarnings("rawtypes") Class clss) {
		return idMap.get(clss);
	}
	
	/**
	 * Creates ID mappings for all indexed XML elements.
	 * 
	 * The indexed XML elements are runQuality, setQuality and Cv, which should all have an ID attribute.
	 * 
	 * @throws IOException
	 */
	private void createIDMappings() throws IOException {
		logger.info("Create ID mappings");
		
		// create ID mappings for all types of indexed elements
		for(String xpath : indexed_xpaths) {
			@SuppressWarnings("rawtypes")
			Class cls = xpathToClass(xpath);
			List<IndexElement> elements = index.getElements(xpath);
			// create ID mappings for all separate elements
			for(IndexElement elem : elements) {
				// get the start tag (including all the attributes, and hence the ID)
				String xmlSnippet = access.getStartTag(elem);
				String id = extractIDFromRawXML(xmlSnippet);
				// combine the XPath expression with the ID to create a unique hash key
				if(id != null) {
					// initialize a HashMap for this class if it doesn't exist yet
					if(idMap.get(cls) == null)
						idMap.put(cls, new LinkedHashMap<String, IndexElement>());
					// store the new mapping
					idMap.get(cls).put(id, elem);
				} else {
					logger.error("Error initializing ID mappings: No ID attribute found for element: {}", xmlSnippet);
	                throw new IllegalStateException("Error initializing ID mappings: No ID attribute found for element: " + xmlSnippet);
	            }
			}
		}
	}
	
	/**
	 * Gives the class type based on XPath node expressions.
	 * 
	 * @param xpath  The XPath node expression for which we want the class type
	 * @return The class type if it corresponds to an indexed XPath node expression, {@code null} else
	 */
	private @SuppressWarnings("rawtypes") Class xpathToClass(String xpath) {
		switch(xpath) {
			case "/qcML/runQuality":
				return QualityAssessment.class;
			case "/qcML/setQuality":
				return QualityAssessment.class;
			case "/qcML/cvList/cv":
				return Cv.class;
			default:
				return null;
		}
	}
	
	/**
	 * Extracts the ID attribute from an XML element.
	 * 
	 * @param xml  The opening tag (containing the attributes) from the XML element
	 * @return The ID attribute from the given XML element if found, {@code null} otherwise
	 */
	private String extractIDFromRawXML(String xml) {
		Matcher match = ID_PATTERN.matcher(xml);
        if(match.find())
            return match.group(1);
        else
        	return null;
	}
	
	/**
	 * Returns the XML snippet specified by the given class and ID attribute.
	 * 
	 * @param cls  The class to which the XML element will be mapped
	 * @param id  The XML ID attribute of the required element
	 * @return The XML snippet corresponding to the specified class with the specified ID attribute if found, {@code null} otherwise
	 */
	public String getXMLSnippet(@SuppressWarnings("rawtypes") Class cls, String id) {
        // check if a mapping for the given class with the given ID exists
		Map<String, IndexElement> mapping = idMap.get(cls);
		if(mapping != null) {
			IndexElement elem = mapping.get(id);
			if(elem != null)
	            return readXML(elem);
			else
				return null;
		}
		else
			return null;
	}

    /**
     * Reads an XML snippet from the current qcML file specified by the given offset range.
     *
     * @param byteRange  The offset range for the requested XML snippet
     * @return The XML snippet specified by the given offset range
     */
	public String readXML(IndexElement byteRange) {
        return readXML(byteRange, 0);
    }

    /**
     * Reads an XML snippet from the current qcML file specified by the given offset range.
     *
     * If the requested snippet is longer than maxChars, only the first maxChars amount of characters are read.
     *
     * @param byteRange  The offset range for the requested XML snippet
     * @param maxChars  The maximum amount of characters to read. If equal to 0, this is ignored.
     * @return The XML snippet specified by the given offset range
     */
    public String readXML(IndexElement byteRange, int maxChars) {
        try {
            if(byteRange != null) {
                long stop; // where we will stop reading
                long limitedStop = byteRange.getStart() + maxChars; // the potential end-point of reading
                // if a limit was specified and the XML element length is longer
                // than the limit, we only read up to the provided limit
                if (maxChars > 0 && byteRange.getStop() > limitedStop) {
                    stop = limitedStop;
                } else { // otherwise we will read up to the end of the XML element
                    stop = byteRange.getStop();
                }
                return xmlExtractor.readString(byteRange.getStart(), stop, qcmlFile);
            } else {
                logger.error("Invalid <null> IndexElement specified to be read");
                throw new IllegalArgumentException("Invalid <null> IndexElement specified to be read");
            }
        } catch (IOException e) {
            logger.error("Could not extract XML from file <{}>: ", qcmlFile, e);
            throw new IllegalArgumentException("Could not extract XML from file: " + qcmlFile);
        }
    }
}
