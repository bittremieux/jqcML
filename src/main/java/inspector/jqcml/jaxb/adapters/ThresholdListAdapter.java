package inspector.jqcml.jaxb.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import inspector.jqcml.model.Threshold;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts between a simple list of {@link Threshold}s (encapsulated in a {@link ThresholdList}) and a {@link Map} of {@link Threshold}s indexed by their id.
 */
public class ThresholdListAdapter extends XmlAdapter<ThresholdList, Map<String, Threshold>> {

	@Override
	public Map<String, Threshold> unmarshal(ThresholdList thrList) throws Exception {
		Map<String, Threshold> thrMap = new TreeMap<>();
		for(Threshold threshold : thrList)
			thrMap.put(threshold.getAccession(), threshold);
		
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
	private List<Threshold> thresholdList;
	
	public ThresholdList() {
		thresholdList = new ArrayList<>();
	}
	
	public ThresholdList(Collection<Threshold> thresholdCollection) {
		this.thresholdList = new ArrayList<>(thresholdCollection);
	}
	
	public void addElement(Threshold elem) {
		thresholdList.add(elem);
	}
	
	public int size() {
		return thresholdList.size();
	}

	public Iterator<Threshold> iterator() {
		return thresholdList.iterator();
	}
	
	
}
