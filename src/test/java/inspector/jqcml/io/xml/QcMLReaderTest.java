package inspector.jqcml.io.xml;

import static org.junit.Assert.*;

import inspector.jqcml.io.xml.QcMLFileReader;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QualityAssessment;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class QcMLReaderTest {
		
	private QcMLFileReader reader;

    @Before
    public void setUp() {
        reader = new QcMLFileReader();
    }

    @Test(expected=NullPointerException.class)
    public void validate_null() {
        reader.validate(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void validate_nonExistingFile() {
        reader.validate("data/test/NonExisting.qcML");
    }

    @Test
    public void validate_noXMLFile() {
        assertFalse(reader.validate("data/test/PlainText.qcML"));
    }

    @Test
    public void validate_invalidXMLFile() {
        assertFalse(reader.validate("data/test/Invalid.qcML"));
    }

    @Test
    public void validate_validQcMLFile() {
        assertTrue(reader.validate("data/test/CvParameterTest.qcML"));
    }

    @Test(expected=NullPointerException.class)
    public void getQcML_null() {
        reader.getQcML(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQcML_nonExistingFile() {
        reader.getQcML("data/test/NonExisting.qcML");
    }

    @Test
    public void getQcML_noXMLFile() {
        assertNull(reader.getQcML("data/test/PlainText.qcML"));
    }

    @Test
    public void getQcML_invalidXMLFile() {
        assertNull(reader.getQcML("data/test/Invalid.qcML"));
    }

    @Test(expected=NullPointerException.class)
    public void getCv_nullFile() {
        reader.getCv(null, "id");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getCv_nonExistingFile() {
        reader.getCv("data/test/NonExisting.qcML", "id");
    }

    @Test
    public void getCv_noXMLFile() {
        assertNull(reader.getCv("data/test/PlainText.qcML", "id"));
    }

    @Test
    public void getCv_invalidXMLFile() {
        assertNull(reader.getCv("data/test/Invalid.qcML", "id"));
    }

    @Test
    public void getCv_nullId() {
        assertNull(reader.getCv("data/test/CvParameterTest.qcML", null));
    }

    @Test
    public void getCv_nonExistingId() {
        assertNull(reader.getCv("data/test/CvParameterTest.qcML", "non-existing id"));
    }

    @Test(expected=NullPointerException.class)
    public void getCvIterator_nullFile() {
        reader.getCvIterator(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getCvIterator_nonExistingFile() {
        reader.getCvIterator("data/test/NonExisting.qcML");
    }

    @Test
    public void getCvIterator_noXMLFile() {
        Iterator<Cv> it = reader.getCvIterator("data/test/PlainText.qcML");

        assertFalse(it.hasNext());
    }

    @Test
    public void getCvIterator_invalidXMLFile() {
        Iterator<Cv> it = reader.getCvIterator("data/test/Invalid.qcML");

        assertFalse(it.hasNext());
    }

    @Test(expected=NullPointerException.class)
    public void getQualityAssessment_nullFile() {
        reader.getQualityAssessment(null, "id");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQualityAssessment_nonExistingFile() {
        reader.getQualityAssessment("data/test/NonExisting.qcML", "id");
    }

    @Test
    public void getQualityAssessment_noXMLFile() {
        assertNull(reader.getQualityAssessment("data/test/PlainText.qcML", "id"));
    }

    @Test
    public void getQualityAssessment_invalidXMLFile() {
        assertNull(reader.getQualityAssessment("data/test/Invalid.qcML", "id"));
    }

    @Test
    public void getQualityAssessment_nullId() {
        assertNull(reader.getQualityAssessment("data/test/CvParameterTest.qcML", null));
    }

    @Test
    public void getQualityAssessment_nonExistingId() {
        assertNull(reader.getQualityAssessment("data/test/CvParameterTest.qcML", "non-existing id"));
    }

    @Test(expected=NullPointerException.class)
    public void getQualityAssessmentIterator_nullFile() {
        reader.getQualityAssessmentIterator(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQualityAssessmentIterator_nonExistingFile() {
        reader.getQualityAssessmentIterator("data/test/NonExisting.qcML");
    }

    @Test
    public void getQualityAssessmentIterator_noXMLFile() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator("data/test/PlainText.qcML");

        assertFalse(it.hasNext());
    }

    @Test
    public void getQualityAssessmentIterator_invalidXMLFile() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator("data/test/Invalid.qcML");

        assertFalse(it.hasNext());
    }

}
