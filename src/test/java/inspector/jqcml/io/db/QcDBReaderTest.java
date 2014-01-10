package inspector.jqcml.io.db;

import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QualityAssessment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.util.Iterator;

import static org.junit.Assert.*;

public class QcDBReaderTest {

    private EntityManagerFactory emf;
	private QcDBReader reader;

    @Before
    public void setUp() {
        emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "mydb", "root", null);
        reader = new QcDBReader(emf);
    }

    @After
    public void tearDown() {
        emf.close();
    }

    @Test
    public void getQcML_null() {
        assertNull(reader.getQcML(null));
    }

    @Test
    public void getQcML_nonExistingQcML() {
        assertNull(reader.getQcML("NonExisting.qcML"));
    }

    @Test
    public void getCv_nonExistingQcML() {
        reader.getCv("NonExisting.qcML", "id");
    }

    @Test
    public void getCv_nullId() {
        assertNull(reader.getCv("CvParameterTest.qcML", null));
    }

    @Test
    public void getCv_nonExistingId() {
        assertNull(reader.getCv("CvParameterTest.qcML", "non-existing id"));
    }

    @Test
    public void getCvIterator_nonExistingQcML() {
        Iterator<Cv> it = reader.getCvIterator("NonExisting.qcML");
        assertFalse(it.hasNext());
    }

    @Test
    public void getQualityAssessment_nonExistingQcML() {
        assertNull(reader.getQualityAssessment("NonExisting.qcML", "id"));
    }

    @Test
    public void getQualityAssessment_nullId() {
        assertNull(reader.getQualityAssessment("CvParameterTest.qcML", null));
    }

    @Test
    public void getQualityAssessment_nonExistingId() {
        assertNull(reader.getQualityAssessment("CvParameterTest.qcML", "non-existing id"));
    }

    @Test
    public void getQualityAssessmentIterator_nonExistingQcML() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator("NonExisting.qcML");
        assertFalse(it.hasNext());
    }

}
