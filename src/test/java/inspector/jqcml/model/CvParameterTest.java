package inspector.jqcml.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inspector.jqcml.io.xml.QcMLFileReader;

public class CvParameterTest {

    private QcML qcml;

    @Before
    public void setUp() {
        qcml = new QcMLFileReader().getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());
    }

    @Test
    public void setUnitCvRef_null() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertNotNull(param.getUnitCvRef());

        param.setUnitCvRef(null);

        assertNull(param.getUnitCvRef());
    }

    @Test(expected=IllegalArgumentException.class)
    public void setUnitCvRef_cvNotPresent() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertNotNull(param.getUnitCvRef());

        Cv cv = new Cv("new cv", "/new/cv/", "new_cv");
        param.setUnitCvRef(cv);
    }

    @Test
    public void setUnitCvRef_valid() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertEquals("cv_0", param.getUnitCvRef().getId());

        Cv cv = new Cv("new cv", "/new/cv/", "new_cv");
        qcml.addCv(cv);
        param.setUnitCvRef(cv);

        assertEquals("new_cv", param.getUnitCvRef().getId());
    }

    @Test(expected=NullPointerException.class)
    public void setCvRef_null() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertNotNull(param.getCvRef());

        param.setCvRef(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void setCvRef_cvNotPresent() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertNotNull(param.getCvRef());

        Cv cv = new Cv("new cv", "/new/cv/", "new_cv");
        param.setCvRef(cv);
    }

    @Test
    public void setCvRef_valid() {
        QualityParameter param = qcml.getRunQuality("run_1").getQualityParameter("QC:000000");

        assertEquals("cv_0", param.getCvRef().getId());

        Cv cv = new Cv("new cv", "/new/cv/", "new_cv");
        qcml.addCv(cv);
        param.setCvRef(cv);

        assertEquals("new_cv", param.getCvRef().getId());
    }

}
