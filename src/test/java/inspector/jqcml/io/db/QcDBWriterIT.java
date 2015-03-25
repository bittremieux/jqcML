package inspector.jqcml.io.db;

import inspector.jqcml.io.xml.QcMLFileReader;
import inspector.jqcml.io.xml.QcMLFileWriter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QcDBWriterIT {

    private static final String PORT = System.getProperty("mysql.port");

    private EntityManagerFactory emf;
    private QcDBWriter writer;

    @Before
    public void setUp() {
        emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
        writer = new QcDBWriter(emf);
    }

    @After
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE table_value").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE table_row").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE table_column").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE table_attachment").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE attachment_parameter").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE threshold").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE quality_parameter").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE meta_data_parameter").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE quality_assessment").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE cv_list").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE cv").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE qcml").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE pk_sequence").executeUpdate();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        em.getTransaction().commit();
        em.close();

        emf.close();
    }

    @Test(expected = NullPointerException.class)
    public void writeQcML_null() {
        writer.writeQcML(null);
    }

    @Test(expected = NullPointerException.class)
    public void writeCv_null() {
        writer.writeCv(null);
    }

    @Test
    public void writeCv_new() {
        Cv cv = new Cv("name", "uri", "id");
        writer.writeCv(cv);
    }

    @Test
    public void writeCv_duplicate() {
        Cv cv = new Cv("name", "uri", "id");
        writer.writeCv(cv);

        Cv cvOther = new Cv("other name", "other uri", "id");
        writer.writeCv(cvOther);
    }

    @Test
    public void writeQcML_nullVersion() {
        QcML qcml = new QcML();
        qcml.setFileName("Null_version.qcML");
        qcml.setVersion(null);

        Cv cv = new Cv("name", "uri", "cv");
        qcml.addCv(cv);

        QualityAssessment run = new QualityAssessment("run");
        QualityParameter param = new QualityParameter("name", cv, "param");
        param.setAccession("accession");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        assertNull(qcml.getVersion());

        // warning should be logged
        writer.writeQcML(qcml);

        assertEquals(QcMLFileWriter.QCML_VERSION, qcml.getVersion());
    }

    @Test
    public void writeQcML_invalidVersion() {
        QcML qcml = new QcML();
        qcml.setFileName("Invalid_version.qcML");
        String version = "My.version.number";
        qcml.setVersion(version);

        Cv cv = new Cv("name", "uri", "cv");
        qcml.addCv(cv);

        QualityAssessment run = new QualityAssessment("run");
        QualityParameter param = new QualityParameter("name", cv, "param");
        param.setAccession("accession");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        assertEquals(version, qcml.getVersion());

        // warning should be logged
        writer.writeQcML(qcml);

        assertEquals(QcMLFileWriter.QCML_VERSION, qcml.getVersion());
    }

    @Test
    public void writeQcML_noSetQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());

        qcml.removeAllSetQualities();

        writer.writeQcML(qcml);
    }

    @Test
    public void writeQcML_noRunQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());

        qcml.removeAllRunQualities();

        writer.writeQcML(qcml);
    }

    @Test
    public void writeQcML_duplicateRunQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());
        writer.writeQcML(qcml);

        QcML qcmlNew = new QcML();
        qcmlNew.setFileName("new.qcml");
        qcml.addRunQuality(new QualityAssessment("run_1"));

        writer.writeQcML(qcmlNew);
    }

    @Test
    public void writeQcML_duplicateSetQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());
        writer.writeQcML(qcml);

        QcML qcmlNew = new QcML();
        qcmlNew.setFileName("new.qcml");
        qcml.addSetQuality(new QualityAssessment("set_1"));

        writer.writeQcML(qcmlNew);
    }
}
