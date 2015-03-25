package inspector.jqcml.jaxb.listener;

import inspector.jqcml.model.*;

import javax.xml.bind.Unmarshaller.Listener;
import java.util.Iterator;

/**
 * Resolves various references that couldn't be set automatically during unmarshalling.
 */
public class QcMLListener extends Listener {

    public QcMLListener() {
        // do nothing
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        // start from the head QcML object, and from there process all children as well
        if (target instanceof QcML) {
            QcML qcml = (QcML) target;
            // resolve all JPA bi-directional relationships (that haven't been processed by an Adapter yet)
            resolveJPARelationships(qcml);
            // resolve all references to Cv's for parameters in this object
            resolveCvRef(qcml);
        }
    }

    /**
     * Resolves the inverse side of the bidirectional relationships required for JPA.
     * These relationships specifically are:
     *  - QualityAssessment     ->  QcML
     *  - Threshold             ->  QualityParameter
     *  - TableAttachment       ->  AttachmentParameter
     *
     * Some relationships are taken care of elsewhere:
     * QualityAssessmentAdapter:
     *  - QualityParameter      ->  QualityAssessment
     *  - AttachmentParameter   ->  QualityAssessment
     * TableAttachmentAdapter:
     *  - TableColumn           ->  TableAttachment
     *  - TableRow              ->  TableAttachment
     *
     * @param qcml  the QcML object for which all relationships will be resolved.
     */
    private void resolveJPARelationships(QcML qcml) {
        // all runQuality's
        for(Iterator<QualityAssessment> qaIt = qcml.getRunQualityIterator(); qaIt.hasNext(); ) {
            QualityAssessment qa = qaIt.next();
            // set parent qcML
            qa.setParentQcML(qcml);
            for(Iterator<QualityParameter> qpIt = qa.getQualityParameterIterator(); qpIt.hasNext(); ) {
                QualityParameter qp = qpIt.next();
                // set parent QP for all thresholds
                for(Iterator<Threshold> thresholdIt = qp.getThresholdIterator(); thresholdIt.hasNext(); ) {
                    thresholdIt.next().setParentQualityParameter(qp);
                }
            }
            for(Iterator<AttachmentParameter> apIt = qa.getAttachmentParameterIterator(); apIt.hasNext(); ) {
                AttachmentParameter ap = apIt.next();
                // set parent AP
                if(ap.getTable() != null) {
                    ap.getTable().setParentAttachment(ap);
                }
            }
        }
        // all setQuality's
        for(Iterator<QualityAssessment> qaIt = qcml.getSetQualityIterator(); qaIt.hasNext(); ) {
            QualityAssessment qa = qaIt.next();
            // set parent qcML
            qa.setParentQcML(qcml);
            for(Iterator<QualityParameter> qpIt = qa.getQualityParameterIterator(); qpIt.hasNext(); ) {
                QualityParameter param = qpIt.next();
                // set parent QP for all thresholds
                for(Iterator<Threshold> thresholdIt = param.getThresholdIterator(); thresholdIt.hasNext(); ) {
                    thresholdIt.next().setParentQualityParameter(param);
                }
            }
            for(Iterator<AttachmentParameter> apIt = qa.getAttachmentParameterIterator(); apIt.hasNext(); ) {
                AttachmentParameter ap = apIt.next();
                if(ap.getTable() != null) {
                    ap.getTable().setParentAttachment(ap);
                }
            }
        }
    }

    /**
     * Resolves references to {@link Cv}s in {@link MetaDataParameter}s, {@link QualityParameter}s, {@link AttachmentParameter}s and {@link Threshold}s.
     *
     * While unmarshalling references to Cvs (i.e. cvRef and unitCvRef) are stored as String values.
     * This is done to ensure that the reference to the Cv is saved, even when the corresponding Cv couldn't be found while unmarshalling.
     * If the references would be resolved on the fly by JAXB using XmlIDREF, if the corresponding CV is not available, the cvRef would simply be {@code null}.
     *
     * Instead, we resolve the references manually using this listener.
     * Therefore, {@link CvParameter}s contain both the ID to the Cv as a String, and a pointer reference to the Cv (if found).
     *
     * @param qcml  the parent {@link QcML} object for which all references to Cvs are resolved.
     */
    private void resolveCvRef(QcML qcml) {
        // all runQuality's
        for (Iterator<QualityAssessment> qaIt = qcml.getRunQualityIterator(); qaIt.hasNext(); ) {
            resolveCvRef(qcml, qaIt.next());
        }
        // all setQuality's
        for (Iterator<QualityAssessment> qaIt = qcml.getSetQualityIterator(); qaIt.hasNext(); ) {
            resolveCvRef(qcml, qaIt.next());
        }
    }

    private void resolveCvRef(QcML qcml, QualityAssessment qa) {
        // all MetaDataParameters
        for(Iterator<MetaDataParameter> mpIt = qa.getMetaDataParameterIterator(); mpIt.hasNext(); ) {
            MetaDataParameter mp = mpIt.next();
            mp.setCvRef(qcml.getCv(mp.getCvRefId()));
        }
        // all QualityParameters
        for(Iterator<QualityParameter> qpIt = qa.getQualityParameterIterator(); qpIt.hasNext(); ) {
            QualityParameter qp = qpIt.next();
            qp.setCvRef(qcml.getCv(qp.getCvRefId()));
            qp.setUnitCvRef(qcml.getCv(qp.getUnitCvRefId()));
            // threshold
            for(Iterator<Threshold> thresholdIt = qp.getThresholdIterator(); thresholdIt.hasNext(); ) {
                Threshold threshold = thresholdIt.next();
                threshold.setCvRef(qcml.getCv(threshold.getCvRefId()));
                threshold.setUnitCvRef(qcml.getCv(threshold.getUnitCvRefId()));
            }
        }
        // all AttachmentParameters
        for(Iterator<AttachmentParameter> apIt = qa.getAttachmentParameterIterator(); apIt.hasNext(); ) {
            AttachmentParameter ap = apIt.next();
            ap.setCvRef(qcml.getCv(ap.getCvRefId()));
            ap.setUnitCvRef(qcml.getCv(ap.getUnitCvRefId()));
        }
    }
}
