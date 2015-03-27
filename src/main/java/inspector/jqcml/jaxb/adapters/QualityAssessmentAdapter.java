package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.*;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts between a {@link QualityAssessmentList} and a {@link QualityAssessment}.
 * 
 * During this conversion the {@link MetaDataParameter}s, {@link QualityParameter}s and {@link AttachmentParameter}s are stored in a separate {@link Map}, indexed by their id.
 */
public class QualityAssessmentAdapter extends XmlAdapter<QualityAssessmentList, QualityAssessment> {

    @Override
    public QualityAssessmentList marshal(QualityAssessment qa) throws Exception {
        QualityAssessmentList qal = new QualityAssessmentList();
        qal.setId(qa.getId());
        qal.setSet(qa.isSet());

        for(Iterator<MetaDataParameter> it = qa.getMetaDataParameterIterator(); it.hasNext(); ) {
            qal.addParameter(it.next());
        }
        for(Iterator<QualityParameter> it = qa.getQualityParameterIterator(); it.hasNext(); ) {
            qal.addParameter(it.next());
        }
        for(Iterator<AttachmentParameter> it = qa.getAttachmentParameterIterator(); it.hasNext(); ) {
            qal.addParameter(it.next());
        }

        return qal;
    }

    @Override
    public QualityAssessment unmarshal(QualityAssessmentList qal) throws Exception {
        QualityAssessment qa = new QualityAssessment(qal.getId());
        qa.setSet(qal.isSet());

        for(CvParameter param : qal) {
            if(param instanceof MetaDataParameter) {
                qa.addMetaDataParameter((MetaDataParameter) param);
                // add bi-directional relationship
                param.setParentQualityAssessment(qa);
            } else if(param instanceof QualityParameter) {
                qa.addQualityParameter((QualityParameter) param);
                // add bi-directional relationship
                param.setParentQualityAssessment(qa);
            } else if(param instanceof AttachmentParameter) {
                qa.addAttachmentParameter((AttachmentParameter) param);
                // add bi-directional relationship
                param.setParentQualityAssessment(qa);
            }
        }

        return qa;
    }
}
