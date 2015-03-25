package inspector.jqcml.io;

import inspector.jqcml.io.db.QcDBManagerFactory;
import inspector.jqcml.io.db.QcDBReader;
import inspector.jqcml.io.db.QcDBWriter;
import inspector.jqcml.io.xml.QcMLFileReader;
import inspector.jqcml.io.xml.QcMLFileWriter;
import inspector.jqcml.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WriteReadFileIT {

    private static final String PORT = System.getProperty("mysql.port");

    private Random random = new Random();

    private QcML qcmlExpected;

    @Before
    public void setUp() {
        // create fully populated qcML object
        qcmlExpected = generateRandomQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
	}

    private QcML generateRandomQcML(String name) {
        QcML qcml = new QcML();
        qcml.setFileName(name);
        // add Cv's
        for(int i = 0; i < 4; i++) {
            Cv cv = new Cv();
            cv.setFullName("cv full name " + i);
            cv.setUri("/uri/to/cv/" + i);
            cv.setId("cv_" + i);
            cv.setVersion(Integer.toString(i));
            qcml.addCv(cv);
        }
        // add runQualities
        for(int run = 0; run < 4; run++) {
            QualityAssessment runQuality = new QualityAssessment("run_" + run);
			// add MetaDataParameters
			for(int p = 0; p < Math.random() * 10; p++) {
				MetaDataParameter param = new MetaDataParameter();
				param.setName("metadata parameter " + p + " name");
				param.setDescription("metadata parameter " + p + " description");
				param.setValue("metadata value " + p);
				param.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
				param.setAccession("accession " + p);
				param.setId("r" + run + "_mp" + p);
				runQuality.addMetaDataParameter(param);
			}
            // add QualityParameters
            for(int p = 0; p < Math.random() * 30; p++) {
                QualityParameter param = new QualityParameter();
                param.setName("quality parameter " + p + " name");
				param.setDescription("quality parameter " + p + " description");
                param.setValue("value " + p);
                param.setUnitAccession("unit accession " + p);
                param.setUnitName("unit name " + p);
                param.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                param.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                param.setAccession("accession " + p);
                param.setId("r" + run + "_qp" + p);
                param.setFlag(Math.random() < 0.5);
                if(param.hasFlag()) {
                    for(int t = 0; t < Math.random() * 3; t++) {
                        Threshold threshold = new Threshold();
                        threshold.setName("threshold " + p + " " + t);
						threshold.setDescription("threshold " + p + " description");
                        threshold.setValue("threshold value " + p + " " + t);
                        threshold.setUnitAccession("threshold unit accession " + p + " " + t);
                        threshold.setUnitName("threshold unit name " + p + " " + t);
                        threshold.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                        threshold.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                        threshold.setAccession("threshold accession " + p + " " + t);
                        threshold.setFileName("threshold file " + p + " " + t);
                        param.addThreshold(threshold);
                    }
                }
                runQuality.addQualityParameter(param);
            }
            // add AttachmentParameter
            for(int p = 0; p < Math.random() * 10; p++) {
                if(Math.random() < 0.5) {
                    // binary attachment
                    AttachmentParameter binaryAttachment = new AttachmentParameter();
                    binaryAttachment.setName("binary attachment parameter name " + run + " " + p);
					binaryAttachment.setDescription("binary attachment parameter " + p + " description");
                    binaryAttachment.setValue(Double.toString(Math.random() * 100000));
                    binaryAttachment.setUnitAccession("binary attachment unit accession " + run + " " + p);
                    binaryAttachment.setUnitName("binary attachment unit name " + run + " " + p);
                    binaryAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setAccession("binary attachment accession " + run + " " + p);
                    binaryAttachment.setQualityParameterRef(
                            runQuality.getQualityParameter("accession " + (int)(Math.random() * runQuality.getNumberOfQualityParameters())));
                    binaryAttachment.setId("r" + run + "_ap" + p);
                    binaryAttachment.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1000, random).toString().getBytes()));
                    runQuality.addAttachmentParameter(binaryAttachment);
                }
                else {
                    // tabular attachment
                    AttachmentParameter tableAttachment = new AttachmentParameter();
                    tableAttachment.setName("table attachment parameter name " + run + " " + p);
					tableAttachment.setDescription("table attachment parameter " + p + " description");
                    tableAttachment.setValue("456");
                    tableAttachment.setUnitAccession("table attachment unit accession " + run + " " + p);
                    tableAttachment.setUnitName("table attachment unit name " + run + " " + p);
                    tableAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setAccession("table attachment accession " + run + " " + p);
                    tableAttachment.setQualityParameterRef(
                            runQuality.getQualityParameter("accession " + (int)(Math.random() * runQuality.getNumberOfQualityParameters())));
                    tableAttachment.setId("r" + run + "_ap" + p);
                    TableAttachment table = new TableAttachment();
                    int rows = 1 + (int)(Math.random() * 100);
                    int cols = 1 + (int)(Math.random() * 10);
                    for(int c = 0; c < cols; c++)
                        table.addColumn("column_" + c);
                    for(int r = 0; r < rows; r++)
                        for(int c = 0; c < cols; c++)
                            table.addValue("column_" + c, r, new BigInteger(32, random).toString(32));
                    tableAttachment.setTable(table);
                    runQuality.addAttachmentParameter(tableAttachment);
                }
            }
            qcml.addRunQuality(runQuality);
        }

        // add setQualities
        for(int set = 0; set < 2; set++) {
            QualityAssessment setQuality = new QualityAssessment("set_" + set);
			// add MetaDataParameters
			for(int p = 0; p < Math.random() * 10; p++) {
				MetaDataParameter param = new MetaDataParameter();
				param.setName("metadata parameter " + p + " name");
				param.setDescription("metadata parameter " + p + " description");
				param.setValue("metadata value " + p);
				param.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
				param.setAccession("accession " + p);
				param.setId("s" + set + "_mp" + p);
				setQuality.addMetaDataParameter(param);
			}
            // add QualityParameters
            for(int p = 0; p < Math.random() * 20; p++) {
                QualityParameter param = new QualityParameter();
                param.setName("quality parameter " + p + " name");
				param.setDescription("quality parameter " + p + " description");
                param.setValue("value " + p);
                param.setUnitAccession("unit accession " + p);
                param.setUnitName("unit name " + p);
                param.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                param.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                param.setAccession("accession " + p);
                param.setId("s" + set + "_qp" + p);
                param.setFlag(Math.random() < 0.5);
                if(param.hasFlag()) {
                    for(int t = 0; t < Math.random() * 3; t++) {
                        Threshold threshold = new Threshold();
                        threshold.setName("threshold " + p + " " + t);
						threshold.setDescription("threshold " + p + " description");
                        threshold.setValue("threshold value " + p + " " + t);
                        threshold.setUnitAccession("threshold unit accession " + p + " " + t);
                        threshold.setUnitName("threshold unit name " + p + " " + t);
                        threshold.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                        threshold.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                        threshold.setAccession("threshold accession " + p + " " + t);
                        threshold.setFileName("threshold file " + p + " " + t);
                        param.addThreshold(threshold);
                    }
                }
                setQuality.addQualityParameter(param);
            }
            // add AttachmentParameter
            for(int p = 0; p < Math.random() * 10; p++) {
                if(Math.random() < 0.5) {
                    // binary attachment
                    AttachmentParameter binaryAttachment = new AttachmentParameter();
                    binaryAttachment.setName("binary attachment parameter name " + set + " " + p);
					binaryAttachment.setDescription("binary attachment parameter " + p + " description");
                    binaryAttachment.setValue(Double.toString(Math.random() * 100000));
                    binaryAttachment.setUnitAccession("binary attachment unit accession " + set + " " + p);
                    binaryAttachment.setUnitName("binary attachment unit name " + set + " " + p);
                    binaryAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setAccession("binary attachment accession " + set + " " + p);
                    binaryAttachment.setQualityParameterRef(
                            setQuality.getQualityParameter("accession " + (int) (Math.random() * setQuality.getNumberOfQualityParameters())));
                    binaryAttachment.setId("s" + set + "_ap" + p);
                    binaryAttachment.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1000, random).toString().getBytes()));
                    setQuality.addAttachmentParameter(binaryAttachment);
                }
                else {
                    // tabular attachment
                    AttachmentParameter tableAttachment = new AttachmentParameter();
                    tableAttachment.setName("table attachment parameter name " + set + " " + p);
					tableAttachment.setDescription("table attachment parameter " + p + " description");
                    tableAttachment.setValue("456");
                    tableAttachment.setUnitAccession("table attachment unit accession " + set + " " + p);
                    tableAttachment.setUnitName("table attachment unit name " + set + " " + p);
                    tableAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setCvRef(qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setAccession("table attachment accession " + set + " " + p);
                    tableAttachment.setQualityParameterRef(
                            setQuality.getQualityParameter("accession " + (int) (Math.random() * setQuality.getNumberOfQualityParameters())));
                    tableAttachment.setId("s" + set + "_ap" + p);
                    TableAttachment table = new TableAttachment();
                    int rows = 1 + (int)(Math.random() * 100);
                    int cols = 1 + (int)(Math.random() * 10);
                    for(int c = 0; c < cols; c++)
                        table.addColumn("column_" + c);
                    for(int r = 0; r < rows; r++)
                        for(int c = 0; c < cols; c++)
                            table.addValue("column_" + c, r, new BigInteger(32, random).toString(32));
                    tableAttachment.setTable(table);
                    setQuality.addAttachmentParameter(tableAttachment);
                }
            }
            qcml.addSetQuality(setQuality);
        }

        return qcml;
    }

    @After
    public void deleteFile() {
		if(getClass().getResource("/WriteReadFileTest.qcML") != null) {
			File file = new File(getClass().getResource("/WriteReadFileTest.qcML").getFile());
			file.delete();
		}
		if(getClass().getResource("/WriteReadFileTest.db") != null) {
			File file = new File(getClass().getResource("/WriteReadFileTest.db").getFile());
			file.delete();
		}
    }

    @After
    public void clearMySQL() {
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DROP TABLE table_value").executeUpdate();
        em.createNativeQuery("DROP TABLE table_row").executeUpdate();
        em.createNativeQuery("DROP TABLE table_column").executeUpdate();
        em.createNativeQuery("DROP TABLE table_attachment").executeUpdate();
        em.createNativeQuery("DROP TABLE attachment_parameter").executeUpdate();
        em.createNativeQuery("DROP TABLE threshold").executeUpdate();
		em.createNativeQuery("DROP TABLE quality_parameter").executeUpdate();
		em.createNativeQuery("DROP TABLE meta_data_parameter").executeUpdate();
        em.createNativeQuery("DROP TABLE quality_assessment").executeUpdate();
        em.createNativeQuery("DROP TABLE cv_list").executeUpdate();
        em.createNativeQuery("DROP TABLE cv").executeUpdate();
        em.createNativeQuery("DROP TABLE qcml").executeUpdate();
        em.createNativeQuery("DROP TABLE pk_sequence").executeUpdate();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @After
    public void clearSQLite() {
        // no need to drop the tables, just delete the database file
        File file = new File("WriteReadFileTest.sqlite");
        file.delete();
    }

    @Test
    public void file() {
        // write the object to a file
        QcMLFileWriter writer = new QcMLFileWriter();
        writer.writeQcML(qcmlExpected);

        // read it back in
        QcMLFileReader reader = new QcMLFileReader();
        QcML qcmlRead = reader.getQcML(getClass().getResource("/WriteReadFileTest.qcML").getFile());
		qcmlRead.setFileName(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");

        assertEquality(qcmlExpected, qcmlRead);
    }

    @Test
    public void mysql_new() {
        // write the object to a MySQL database
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
        QcDBWriter writer = new QcDBWriter(emf);
        writer.writeQcML(qcmlExpected);

        // read it back in
        QcDBReader reader = new QcDBReader(emf);
		QcML qcmlRead = reader.getQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        emf.close();

        assertEquality(qcmlExpected, qcmlRead);
    }

    @Test
    public void mysql_overwrite() {
        // write the object to a MySQL database
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
        QcDBWriter writer = new QcDBWriter(emf);
        writer.writeQcML(qcmlExpected);
        // overwrite the first object
        QcML qcmlOther = generateRandomQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        writer.writeQcML(qcmlOther);

        // read the second object back in
        QcDBReader reader = new QcDBReader(emf);
        QcML qcmlRead = reader.getQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        emf.close();

        assertEquality(qcmlOther, qcmlRead);
    }

    @Test
    public void sqlite_new() {
        // write the object to a SQLite database
        EntityManagerFactory emf = QcDBManagerFactory.createSQLiteFactory("WriteReadFileTest.sqlite");
        QcDBWriter writer = new QcDBWriter(emf);
        writer.writeQcML(qcmlExpected);

        // read it back in
        QcDBReader reader = new QcDBReader(emf);
        QcML qcmlRead = reader.getQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        emf.close();

        assertEquality(qcmlExpected, qcmlRead);
    }

    @Test
    public void sqlite_overwrite() {
        // write the object to a MySQL database
        EntityManagerFactory emf = QcDBManagerFactory.createSQLiteFactory("WriteReadFileTest.sqlite");
        QcDBWriter writer = new QcDBWriter(emf);
        writer.writeQcML(qcmlExpected);
        // overwrite the first object
        QcML qcmlOther = generateRandomQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        writer.writeQcML(qcmlOther);

        // read the second object back in
        QcDBReader reader = new QcDBReader(emf);
        QcML qcmlRead = reader.getQcML(getClass().getResource("/").getPath() + "WriteReadFileTest.qcML");
        emf.close();

        assertEquality(qcmlOther, qcmlRead);
    }

    private void assertEquality(QcML qcmlExpected, QcML qcmlRead) {
        // compare both objects
        assertEquals(qcmlExpected.getFileName(), qcmlRead.getFileName());
        // Cv's
        assertEquals(qcmlExpected.getNumberOfCvs(), qcmlRead.getNumberOfCvs());
        for(Iterator<Cv> cvIt = qcmlExpected.getCvIterator(); cvIt.hasNext(); ) {
            Cv cv = cvIt.next();
            Cv cvOther = qcmlRead.getCv(cv.getId());
            assertNotNull(cvOther);
            assertEquals(cv, cvOther);
        }
        // runQualities
        assertEquals(qcmlExpected.getNumberOfRunQualities(), qcmlRead.getNumberOfRunQualities());
        for(Iterator<QualityAssessment> it = qcmlExpected.getRunQualityIterator(); it.hasNext(); ) {
            QualityAssessment qa = it.next();
            QualityAssessment qaOther = qcmlRead.getRunQuality(qa.getId());
            assertNotNull(qaOther);
            assertEquals(qa.getId(), qaOther.getId());
            assertEquals(qa.isSet(), qaOther.isSet());

			// MetaDataParameters
			assertEquals(qa.getNumberOfMetaDataParameters(), qaOther.getNumberOfMetaDataParameters());
			for(Iterator<MetaDataParameter> paramIt = qa.getMetaDataParameterIterator(); paramIt.hasNext(); ) {
				MetaDataParameter param = paramIt.next();
				MetaDataParameter paramOther = qaOther.getMetaDataParameter(param.getAccession());
				assertNotNull(paramOther);
				assertEquals(param, paramOther);
			}

            // QualityParameters
            assertEquals(qa.getNumberOfQualityParameters(), qaOther.getNumberOfQualityParameters());
            for(Iterator<QualityParameter> paramIt = qa.getQualityParameterIterator(); paramIt.hasNext(); ) {
                QualityParameter param = paramIt.next();
                QualityParameter paramOther = qaOther.getQualityParameter(param.getAccession());
                assertNotNull(paramOther);
                assertEquals(param, paramOther);
            }

            // AttachmentParameters
            assertEquals(qa.getNumberOfAttachmentParameters(), qaOther.getNumberOfAttachmentParameters());
            for(Iterator<AttachmentParameter> paramIt = qa.getAttachmentParameterIterator(); paramIt.hasNext(); ) {
                AttachmentParameter param = paramIt.next();
                AttachmentParameter paramOther = qaOther.getAttachmentParameter(param.getAccession());
                assertNotNull(paramOther);
                assertEquals(param, paramOther);
            }
        }
        // setQualities
        assertEquals(qcmlExpected.getNumberOfSetQualities(), qcmlRead.getNumberOfSetQualities());
        for(Iterator<QualityAssessment> it = qcmlExpected.getSetQualityIterator(); it.hasNext(); ) {
            QualityAssessment qa = it.next();
            QualityAssessment qaOther = qcmlRead.getSetQuality(qa.getId());
            assertNotNull(qaOther);
            assertEquals(qa.getId(), qaOther.getId());
            assertEquals(qa.isSet(), qaOther.isSet());

			// MetaDataParameters
			assertEquals(qa.getNumberOfMetaDataParameters(), qaOther.getNumberOfMetaDataParameters());
			for(Iterator<MetaDataParameter> paramIt = qa.getMetaDataParameterIterator(); paramIt.hasNext(); ) {
				MetaDataParameter param = paramIt.next();
				MetaDataParameter paramOther = qaOther.getMetaDataParameter(param.getAccession());
				assertNotNull(paramOther);
				assertEquals(param, paramOther);
			}

            // QualityParameters
            assertEquals(qa.getNumberOfQualityParameters(), qaOther.getNumberOfQualityParameters());
            for(Iterator<QualityParameter> paramIt = qa.getQualityParameterIterator(); paramIt.hasNext(); ) {
                QualityParameter param = paramIt.next();
                QualityParameter paramOther = qaOther.getQualityParameter(param.getAccession());
                assertNotNull(paramOther);
                assertEquals(param, paramOther);
            }

            // AttachmentParameters
            assertEquals(qa.getNumberOfAttachmentParameters(), qaOther.getNumberOfAttachmentParameters());
            for(Iterator<AttachmentParameter> paramIt = qa.getAttachmentParameterIterator(); paramIt.hasNext(); ) {
                AttachmentParameter param = paramIt.next();
                AttachmentParameter paramOther = qaOther.getAttachmentParameter(param.getAccession());
                assertNotNull(paramOther);
                assertEquals(param, paramOther);
            }
        }
    }

}
