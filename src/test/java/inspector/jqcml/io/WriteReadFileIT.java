package inspector.jqcml.io;

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
import java.util.Objects;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WriteReadFileIT {

    private static final String PORT = System.getProperty("mysql.port");

    private Random random = new Random();

    private QcML qcmlExpected;

    @Before
    public void setUp() {
        // create fully populated qcML object
        qcmlExpected = generateRandomQcML("WriteReadFileTest.qcML");
    }

    private QcML generateRandomQcML(String name) {
        QcML qcml = new QcML();
        qcml.setFileName(name);
        // add Cv's
        for(int i = 0; i < 4; i++) {
            Cv cv = new Cv("cv full name " + i, "/uri/to/cv/" + i, "cv_" + i);
            cv.setVersion(Integer.toString(i));
            qcml.addCv(cv);
        }
        // add runQualities
        for(int run = 0; run < 4; run++) {
            QualityAssessment runQuality = new QualityAssessment("run_" + run, false);
            // add MetaDataParameters
            for(int p = 0; p < Math.random() * 10; p++) {
                MetaDataParameter param = new MetaDataParameter("metadata parameter " + p + " name",
                        qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())), "accession " + p,
                        "r" + run + "_mp" + p);
                param.setDescription("metadata parameter " + p + " description");
                param.setValue("metadata value " + p);
                runQuality.addMetaDataParameter(param);
            }
            // add QualityParameters
            for(int p = 0; p < Math.random() * 30; p++) {
                QualityParameter param = new QualityParameter("quality parameter " + p + " name",
                        qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())), "accession " + p,
                        "r" + run + "_qp" + p);
                param.setDescription("quality parameter " + p + " description");
                param.setValue("value " + p);
                param.setUnitAccession("unit accession " + p);
                param.setUnitName("unit name " + p);
                param.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                param.setFlag(Math.random() < 0.5);
                if(param.hasFlag()) {
                    for(int t = 0; t < Math.random() * 3; t++) {
                        Threshold threshold = new Threshold("threshold " + p + " " + t,
                                qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                                "threshold accession " + p + " " + t);
                        threshold.setDescription("threshold " + p + " description");
                        threshold.setValue("threshold value " + p + " " + t);
                        threshold.setUnitAccession("threshold unit accession " + p + " " + t);
                        threshold.setUnitName("threshold unit name " + p + " " + t);
                        threshold.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
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
                    AttachmentParameter binaryAttachment = new AttachmentParameter("binary attachment parameter name " + run + " " + p,
                            qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                            "binary attachment accession " + run + " " + p, "r" + run + "_ap" + p);
                    binaryAttachment.setDescription("binary attachment parameter " + p + " description");
                    binaryAttachment.setValue(Double.toString(Math.random() * 100000));
                    binaryAttachment.setUnitAccession("binary attachment unit accession " + run + " " + p);
                    binaryAttachment.setUnitName("binary attachment unit name " + run + " " + p);
                    binaryAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setQualityParameterRef(runQuality.getQualityParameter(
                            "accession " + (int) (Math.random() * runQuality.getNumberOfQualityParameters())));
                    binaryAttachment.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1000, random).toString().getBytes()));
                    runQuality.addAttachmentParameter(binaryAttachment);
                }
                else {
                    // tabular attachment
                    AttachmentParameter tableAttachment = new AttachmentParameter("table attachment parameter name " + run + " " + p,
                            qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                            "table attachment accession " + run + " " + p, "r" + run + "_ap" + p);
                    tableAttachment.setDescription("table attachment parameter " + p + " description");
                    tableAttachment.setValue("456");
                    tableAttachment.setUnitAccession("table attachment unit accession " + run + " " + p);
                    tableAttachment.setUnitName("table attachment unit name " + run + " " + p);
                    tableAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setQualityParameterRef(runQuality.getQualityParameter(
                            "accession " + (int) (Math.random() * runQuality.getNumberOfQualityParameters())));
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
            QualityAssessment setQuality = new QualityAssessment("set_" + set, true);
            // add MetaDataParameters
            for(int p = 0; p < Math.random() * 10; p++) {
                MetaDataParameter param = new MetaDataParameter("metadata parameter " + p + " name",
                        qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())), "accession " + p,
                        "s" + set + "_mp" + p);
                param.setDescription("metadata parameter " + p + " description");
                param.setValue("metadata value " + p);
                setQuality.addMetaDataParameter(param);
            }
            // add QualityParameters
            for(int p = 0; p < Math.random() * 20; p++) {
                QualityParameter param = new QualityParameter("quality parameter " + p + " name",
                        qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())), "accession " + p,
                        "s" + set + "_qp" + p);
                param.setDescription("quality parameter " + p + " description");
                param.setValue("value " + p);
                param.setUnitAccession("unit accession " + p);
                param.setUnitName("unit name " + p);
                param.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                param.setFlag(Math.random() < 0.5);
                if(param.hasFlag()) {
                    for(int t = 0; t < Math.random() * 3; t++) {
                        Threshold threshold = new Threshold("threshold " + p + " " + t,
                                qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                                "threshold accession " + p + " " + t);
                        threshold.setDescription("threshold " + p + " description");
                        threshold.setValue("threshold value " + p + " " + t);
                        threshold.setUnitAccession("threshold unit accession " + p + " " + t);
                        threshold.setUnitName("threshold unit name " + p + " " + t);
                        threshold.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
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
                    AttachmentParameter binaryAttachment = new AttachmentParameter("binary attachment parameter name " + set + " " + p,
                            qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                            "binary attachment accession " + set + " " + p, "s" + set + "_ap" + p);
                    binaryAttachment.setDescription("binary attachment parameter " + p + " description");
                    binaryAttachment.setValue(Double.toString(Math.random() * 100000));
                    binaryAttachment.setUnitAccession("binary attachment unit accession " + set + " " + p);
                    binaryAttachment.setUnitName("binary attachment unit name " + set + " " + p);
                    binaryAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    binaryAttachment.setQualityParameterRef(setQuality.getQualityParameter(
                            "accession " + (int) (Math.random() * setQuality.getNumberOfQualityParameters())));
                    binaryAttachment.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1000, random).toString().getBytes()));
                    setQuality.addAttachmentParameter(binaryAttachment);
                }
                else {
                    // tabular attachment
                    AttachmentParameter tableAttachment = new AttachmentParameter("table attachment parameter name " + set + " " + p,
                            qcml.getCv("cv_" + (int)(Math.random() * qcml.getNumberOfCvs())),
                            "table attachment accession " + set + " " + p, "s" + set + "_ap" + p);
                    tableAttachment.setDescription("table attachment parameter " + p + " description");
                    tableAttachment.setValue("456");
                    tableAttachment.setUnitAccession("table attachment unit accession " + set + " " + p);
                    tableAttachment.setUnitName("table attachment unit name " + set + " " + p);
                    tableAttachment.setUnitCvRef(qcml.getCv("cv_" + (int) (Math.random() * qcml.getNumberOfCvs())));
                    tableAttachment.setQualityParameterRef(setQuality.getQualityParameter(
                            "accession " + (int) (Math.random() * setQuality.getNumberOfQualityParameters())));
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
        File file = new File("WriteReadFileTest.qcML");
        file.delete();
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
        QcML qcmlRead = reader.getQcML("WriteReadFileTest.qcML");
        qcmlRead.setFileName("WriteReadFileTest.qcML");

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
        QcML qcmlRead = reader.getQcML("WriteReadFileTest.qcML");
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
        QcML qcmlOther = generateRandomQcML("WriteReadFileTest.qcML");
        writer.writeQcML(qcmlOther);

        // read the second object back in
        QcDBReader reader = new QcDBReader(emf);
        QcML qcmlRead = reader.getQcML("WriteReadFileTest.qcML");
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
        QcML qcmlRead = reader.getQcML("WriteReadFileTest.qcML");
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
        QcML qcmlOther = generateRandomQcML("WriteReadFileTest.qcML");
        writer.writeQcML(qcmlOther);

        // read the second object back in
        QcDBReader reader = new QcDBReader(emf);
        QcML qcmlRead = reader.getQcML("WriteReadFileTest.qcML");
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
                assertTrue(equalsParameters(param, paramOther));
            }

            // QualityParameters
            assertEquals(qa.getNumberOfQualityParameters(), qaOther.getNumberOfQualityParameters());
            for(Iterator<QualityParameter> paramIt = qa.getQualityParameterIterator(); paramIt.hasNext(); ) {
                QualityParameter param = paramIt.next();
                QualityParameter paramOther = qaOther.getQualityParameter(param.getAccession());
                assertNotNull(paramOther);
                assertTrue(equalsParameters(param, paramOther));
            }

            // AttachmentParameters
            assertEquals(qa.getNumberOfAttachmentParameters(), qaOther.getNumberOfAttachmentParameters());
            for(Iterator<AttachmentParameter> paramIt = qa.getAttachmentParameterIterator(); paramIt.hasNext(); ) {
                AttachmentParameter param = paramIt.next();
                AttachmentParameter paramOther = qaOther.getAttachmentParameter(param.getAccession());
                assertNotNull(paramOther);
                assertTrue(equalsParameters(param, paramOther));
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
                assertTrue(equalsParameters(param, paramOther));
            }

            // QualityParameters
            assertEquals(qa.getNumberOfQualityParameters(), qaOther.getNumberOfQualityParameters());
            for(Iterator<QualityParameter> paramIt = qa.getQualityParameterIterator(); paramIt.hasNext(); ) {
                QualityParameter param = paramIt.next();
                QualityParameter paramOther = qaOther.getQualityParameter(param.getAccession());
                assertNotNull(paramOther);
                assertTrue(equalsParameters(param, paramOther));
            }

            // AttachmentParameters
            assertEquals(qa.getNumberOfAttachmentParameters(), qaOther.getNumberOfAttachmentParameters());
            for(Iterator<AttachmentParameter> paramIt = qa.getAttachmentParameterIterator(); paramIt.hasNext(); ) {
                AttachmentParameter param = paramIt.next();
                AttachmentParameter paramOther = qaOther.getAttachmentParameter(param.getAccession());
                assertNotNull(paramOther);
                assertTrue(equalsParameters(param, paramOther));
            }
        }
    }

    private boolean equalsParameters(AbstractParameter param1, AbstractParameter param2) {
        boolean abstractEquals = Objects.equals(param1.getName(), param2.getName()) &&
                Objects.equals(param1.getDescription(), param2.getDescription()) &&
                Objects.equals(param1.getValue(), param2.getValue()) &&
                Objects.equals(param1.getUnitAccession(), param2.getUnitAccession()) &&
                Objects.equals(param1.getUnitName(), param2.getUnitName()) &&
                Objects.equals(param1.getUnitCvRefId(), param2.getUnitCvRefId()) &&
                Objects.equals(param1.getUnitCvRef(), param2.getUnitCvRef());
        boolean metadataEquals = true;
        if(param1 instanceof MetaDataParameter) {
            if(param2 instanceof MetaDataParameter) {
                metadataEquals = Objects.equals(((MetaDataParameter) param1).getId(), ((MetaDataParameter) param2).getId());
            } else {
                metadataEquals = false;
            }
        }
        boolean qualityEquals = true;
        if(param1 instanceof QualityParameter) {
            if(param2 instanceof QualityParameter) {
                QualityParameter qp1 = (QualityParameter) param1;
                QualityParameter qp2 = (QualityParameter) param2;
                qualityEquals = Objects.equals(qp1.getId(), qp2.getId()) &&
                        Objects.equals(qp1.hasFlag(), qp2.hasFlag());
                for(Iterator<Threshold> it = qp1.getThresholdIterator(); it.hasNext(); ) {
                    Threshold tr1 = it.next();
                    Threshold tr2 = qp2.getThreshold(tr1.getAccession());
                    qualityEquals &= equalsParameters(tr1, tr2);
                }
            } else {
                qualityEquals = false;
            }
        }
        boolean attachmentEquals = true;
        if(param1 instanceof AttachmentParameter) {
            if(param2 instanceof AttachmentParameter) {
                AttachmentParameter ap1 = (AttachmentParameter) param1;
                AttachmentParameter ap2 = (AttachmentParameter) param2;
                attachmentEquals = Objects.equals(ap1.getId(), ap2.getId()) &&
                        Objects.equals(ap1.getBinary(), ap2.getBinary()) &&
                        equalsTable(ap1.getTable(), ap2.getTable()) &&
                        equalsParameters(ap1.getQualityParameterRef(), ap2.getQualityParameterRef());
            } else {
                attachmentEquals = false;
            }
        }
        boolean thresholdEquals = true;
        if(param1 instanceof Threshold) {
            if(param2 instanceof Threshold) {
                thresholdEquals = Objects.equals(((Threshold) param1).getFileName(), ((Threshold) param2).getFileName());
            } else {
                thresholdEquals = false;
            }
        }
        return abstractEquals && metadataEquals && qualityEquals && attachmentEquals && thresholdEquals;
    }

    private boolean equalsTable(TableAttachment table1, TableAttachment table2) {
        if(table1 == null && table2 == null) {
            return true;
        } else if(table1 == null || table2 == null) {
            return false;
        } else {
            if(Objects.equals(table1.getRows(), table2.getRows()) &&
                    Objects.equals(table1.getColumns(), table2.getColumns())) {
                for(TableRow row : table1.getRows()) {
                    for(TableColumn column : table1.getColumns()) {
                        if(!table1.getValue(column.getColumn(), row.getRow()).equals(
                                table2.getValue(column.getColumn(), row.getRow()))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
}
