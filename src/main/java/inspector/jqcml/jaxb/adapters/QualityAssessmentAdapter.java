package inspector.jqcml.jaxb.adapters;

/*
 * #%L
 * jqcML
 * %%
 * Copyright (C) 2013 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
        QualityAssessment qa = new QualityAssessment(qal.getId(), qal.isSet());

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
