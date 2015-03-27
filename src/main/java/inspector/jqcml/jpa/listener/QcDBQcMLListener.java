package inspector.jqcml.jpa.listener;

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
