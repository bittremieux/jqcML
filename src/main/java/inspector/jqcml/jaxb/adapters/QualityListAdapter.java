package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.QualityAssessment;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts between a simple list of {@link QualityAssessment}s (encapsulated in a {@link QualityList}) and a {@link Map} of {@link QualityAssessment}s indexed by their id.
 * 
 * @param <T>  Indicates whether we are converting a {@code runQuality} or a {@code setQuality}
 */
public class QualityListAdapter<T extends QualityList> extends XmlAdapter<T, Map<String, QualityAssessment>> {

    protected boolean isSet;
    protected Class<T> myType;

    @Override
    public T marshal(Map<String, QualityAssessment> qaMap) throws Exception {
        return myType.getConstructor(Collection.class).newInstance(qaMap.values());
    }

    @Override
    public Map<String, QualityAssessment> unmarshal(T qaList) throws Exception {
        Map<String, QualityAssessment> qaMap = new TreeMap<>();
        for(QualityAssessment qa : qaList) {
            // set run/setQuality flag
            qa.setSet(isSet);
            // add it to the map
            qaMap.put(qa.getId(), qa);
        }

        return qaMap;
    }

}
