package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.Threshold;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.*;

/**
 * Converts between a simple list of {@link Threshold}s (encapsulated in a {@link ThresholdList}) and a {@link Map} of {@link Threshold}s indexed by their id.
 */
public class ThresholdListAdapter extends XmlAdapter<ThresholdList, Map<String, Threshold>> {

    @Override
    public Map<String, Threshold> unmarshal(ThresholdList thrList) throws Exception {
        Map<String, Threshold> thrMap = new TreeMap<>();
        for(Threshold threshold : thrList) {
            thrMap.put(threshold.getAccession(), threshold);
        }

        return thrMap;
    }

    @Override
    public ThresholdList marshal(Map<String, Threshold> thrMap) throws Exception {
        return new ThresholdList(thrMap.values());
    }

}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link Threshold} objects.
 */
@XmlType(name="thresholdListType")
class ThresholdList implements Iterable<Threshold> {

    @XmlElement(name="threshold")
    private List<Threshold> thresholds;

    public ThresholdList() {
        thresholds = new ArrayList<>();
    }

    public ThresholdList(Collection<Threshold> thresholdCollection) {
        this.thresholds = new ArrayList<>(thresholdCollection);
    }

    public void addElement(Threshold elem) {
        thresholds.add(elem);
    }

    public int size() {
        return thresholds.size();
    }

    public Iterator<Threshold> iterator() {
        return thresholds.iterator();
    }


}
