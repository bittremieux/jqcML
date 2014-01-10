package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.io.xml.QcMLUnmarshaller;
import inspector.jqcml.io.xml.index.QcMLIndexer;
import inspector.jqcml.model.AttachmentParameter;
import inspector.jqcml.model.CvParameter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityAssessmentList;
import inspector.jqcml.model.QualityParameter;

import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts between a {@link QualityAssessmentList} and a {@link QualityAssessment}.
 * 
 * During this conversion the {@link QualityParameter}s and {@link AttachmentParameter}s are stored in a separate {@link Map}, indexed by their id.
 */
public class QualityAssessmentAdapter extends XmlAdapter<QualityAssessmentList, QualityAssessment> {

	@Override
	public QualityAssessmentList marshal(QualityAssessment qa) throws Exception {
		QualityAssessmentList qal = new QualityAssessmentList();
		qal.setId(qa.getId());
		qal.setSet(qa.isSet());

		for(Iterator<QualityParameter> it = qa.getQualityParameterIterator(); it.hasNext(); )
			qal.addParameter(it.next());
		for(Iterator<AttachmentParameter> it = qa.getAttachmentParameterIterator(); it.hasNext(); )
			qal.addParameter(it.next());
		
		return qal;
	}

	@Override
	public QualityAssessment unmarshal(QualityAssessmentList qal) throws Exception {
		QualityAssessment qa = new QualityAssessment(qal.getId());
		qa.setSet(qal.isSet());
		
		for(CvParameter param : qal) {
			if(param instanceof QualityParameter) {
				qa.addQualityParameter((QualityParameter) param);
				// add bi-directional relationship
				param.setParentQualityAssessment(qa);
			}
			else if(param instanceof AttachmentParameter) {
				qa.addAttachmentParameter((AttachmentParameter) param);
				// add bi-directional relationship
				param.setParentQualityAssessment(qa);
			}
		}
		
		return qa;
	}
	
	/**
	 * Resolves references to a {@link Cv} for all parameters in the given {@link QualityAssessment}.
	 * 
	 * If the referenced Cv has already been unmarshalled in the past and is available in the Cv cache, it isn't unmarshalled again.
	 * If the referenced Cv isn't available in the cache, it is unmarshalled using the {@link QcMLUnmarshaller}.
	 * 
	 * @param qa  the QualityAssessment containing the parameters for which the references will be resolved
	 * @param cvCache  a cache containing the previously unmarshalled Cv's
	 * @param index  the {@link QcMLIndexer} used to unmarshal
	 * @param unmarshaller  the {@link QcMLUnmarshaller} used to unmarshal
	 */
	public void resolveReferences(QualityAssessment qa, Map<String, Cv> cvCache, QcMLIndexer index, QcMLUnmarshaller unmarshaller) {
		// for each QualityParameter
		for(Iterator<QualityParameter> it = qa.getQualityParameterIterator(); it.hasNext(); )
			resolveReferences(it.next(), cvCache, index, unmarshaller);
		// for each AttachmentParameter
		for(Iterator<AttachmentParameter> it = qa.getAttachmentParameterIterator(); it.hasNext(); )
			resolveReferences(it.next(), cvCache, index, unmarshaller);
	}
	
	private void resolveReferences(CvParameter param, Map<String, Cv> cvCache, QcMLIndexer index, QcMLUnmarshaller unmarshaller) {
		// cvRef
		String cvId = param.getCvRefId();
		Cv cvRef = resolveReference(cvId, cvCache, index, unmarshaller);
		param.setCvRef(cvRef);
		
		// unitCvRef
		String unitCvId = param.getUnitCvRefId();
		Cv unitCvRef = resolveReference(unitCvId, cvCache, index, unmarshaller);
		param.setUnitCvRef(unitCvRef);
	}
	
	private Cv resolveReference(String id, Map<String, Cv> cvCache, QcMLIndexer index, QcMLUnmarshaller unmarshaller) {
		if(cvCache.containsKey(id))	// Cv already unmarshalled and found in cache
			return cvCache.get(id);
		else {	// new Cv
			// unmarshal the Cv
			String xmlSnippet = index.getXMLSnippet(Cv.class, id);
			if(xmlSnippet != null) {	// can be null if the Cv isn't found in the file
				Cv cv = unmarshaller.unmarshal(xmlSnippet, Cv.class);
				// if this is a viable Cv, store it in the cache
				if(cv != null) {
					cvCache.put(id, cv);
					return cv;
				}
			}
		}
		return null;
	}

}
