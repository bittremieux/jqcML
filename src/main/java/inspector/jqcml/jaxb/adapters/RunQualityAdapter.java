package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.QualityAssessment;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * Converts between a simple list of {@link QualityAssessment}s representing a {@code runQuality} and a {@link Map} of {@link QualityAssessment}s representing a {@code runQuality} indexed by their id.
 */
public class RunQualityAdapter extends QualityListAdapter<RunQualityList> {
	
	public RunQualityAdapter() {
		isSet = false;
		myType = RunQualityList.class;
	}
}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link QualityAssessment} objects representing a {@code runQuality}.
 */
class RunQualityList extends QualityList {
	
	public RunQualityList() {
		super();
	}
	
	public RunQualityList(Collection<QualityAssessment> qaCollection) {
		super(qaCollection);
	}
	
	@XmlElement(name="runQuality")
	@Override
	public List<QualityAssessment> getQualityAssessmentList() {
		return super.getQualityAssessmentList();
	}

	@Override
	public void setQualityAssessmentList(List<QualityAssessment> qaList) {
		super.setQualityAssessmentList(qaList);
	}
	
	
}
