package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.Cv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts between a simple list of {@link Cv}s (encapsulated in a {@link CvList}) and a {@link Map} of {@link Cv}s indexed by their id.
 */
public class CvListAdapter extends XmlAdapter<CvList, Map<String, Cv>> {

	@Override
	public CvList marshal(Map<String, Cv> cvMap) throws Exception {
		return new CvList(cvMap.values());
	}

	@Override
	public Map<String, Cv> unmarshal(CvList cvList) throws Exception {
		Map<String, Cv> cvMap = new TreeMap<>();
		for(Cv cv : cvList)
			cvMap.put(cv.getId(), cv);
		
		return cvMap;
	}

}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link Cv} objects.
 */
@XmlType(name="cvListType")
class CvList implements Iterable<Cv> {
	
	@XmlElement(name="cv", required=true)
	private List<Cv> cvList;
	
	public CvList() {
		cvList = new ArrayList<>();
	}
	
	public CvList(Collection<Cv> cvCollection) {
		this.cvList = new ArrayList<>(cvCollection);
	}
	
	public void addElement(Cv elem) {
		cvList.add(elem);
	}
	
	public int size() {
		return cvList.size();
	}

	public Iterator<Cv> iterator() {
		return cvList.iterator();
	}
	
	
}
