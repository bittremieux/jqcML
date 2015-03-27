package inspector.jqcml.io;

import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;

/**
 * A qcML output writer.
 */
public interface QcMLWriter {

    /** The version of the current qcML XML schema **/
    public static final String QCML_VERSION = "0.0.8";

    /**
     * Writes a {@link QcML} object to the writer's source.
     *
     * @param qcml  The {@link QcML} object to be written. If required the file name will be retrieved from the QcML object.
     */
    public abstract void writeQcML(QcML qcml);

    /**
     * Writes a {@link Cv} object to the writer's source.
     *
     * @param cv  The {@link Cv} object to be written.
     */
    public abstract void writeCv(Cv cv);

    // unimplemented method
    /*
     * Writes a {@link QualityAssessment} object to the writer's source.
     *
     * @param qa  The {@link QualityAssessment} object to be written.
     */
    //public abstract void writeQualityAssessment(QualityAssessment qa);

}
