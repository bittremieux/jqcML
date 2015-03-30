package inspector.jqcml.jpa.listener;

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

import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;

import javax.persistence.PostLoad;
import java.util.Iterator;

/**
 * Filters out duplicate {@link QualityAssessment}s in a {@link QcML} object after retrieval from a qcDB.
 */
public class QcDBQcMLListener {

    /**
     * Due to an incomplete JPA mapping, {@link QualityAssessment}s are duplicated in both the runQuality and setQuality Maps of a {@link QcML} object.
     * Filter out the QualityAssessments that are present in the wrong Map after this QcML has been loaded.
     *
     * @param qcml  the QcML object for which the duplicate QualityAssessments will be filtered.
     */
    @PostLoad
    private void filterDuplicateQA(QcML qcml) {
        for(Iterator<QualityAssessment> it = qcml.getRunQualityIterator(); it.hasNext(); ) {
            QualityAssessment qa = it.next();
            if(qa.isSet()) {
                it.remove();
            }
        }
        for(Iterator<QualityAssessment> it = qcml.getSetQualityIterator(); it.hasNext(); ) {
            QualityAssessment qa = it.next();
            if(!qa.isSet()) {
                it.remove();
            }
        }
    }

}
