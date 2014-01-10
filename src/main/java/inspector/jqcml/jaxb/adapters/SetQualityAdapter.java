package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.QualityAssessment;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * Converts between a simple list of {@link QualityAssessment}s representing a {@code setQuality} and a {@link Map} of {@link QualityAssessment}s representing a {@code setQuality} indexed by their id.
 */
public class SetQualityAdapter extends QualityListAdapter<SetQualityList> {
	
	public SetQualityAdapter() {
		isSet = true;
		myType = SetQualityList.class;
	}
}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link QualityAssessment} objects representing a {@code setQuality}.
 */
class SetQualityList extends QualityList {
	
	public SetQualityList() {
		super();
	}
	
	public SetQualityList(Collection<QualityAssessment> qaCollection) {
		super(qaCollection);
	}
	
	@XmlElement(name="setQuality")
	@Override
	public List<QualityAssessment> getQualityAssessmentList() {
		return super.getQualityAssessmentList();
	}

	@Override
	public void setQualityAssessmentList(List<QualityAssessment> qaList) {
		super.setQualityAssessmentList(qaList);
	}
	
	
}
