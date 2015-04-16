package inspector.jqcml.io.db;

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

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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

        QualityAssessment run = new QualityAssessment("run", false);
        QualityParameter param = new QualityParameter("name", cv, "accession", "param");
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

        QualityAssessment run = new QualityAssessment("run", false);
        QualityParameter param = new QualityParameter("name", cv, "accession", "param");
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
        QcML qcml = fileReader.getQcML(loadResource("/CvParameterTest.qcML").getAbsolutePath());

        qcml.removeAllSetQualities();

        writer.writeQcML(qcml);
    }

    @Test
    public void writeQcML_noRunQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(loadResource("/CvParameterTest.qcML").getAbsolutePath());

        qcml.removeAllRunQualities();

        writer.writeQcML(qcml);
    }

    @Test
    public void writeQcML_duplicateRunQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(loadResource("/CvParameterTest.qcML").getAbsolutePath());
        writer.writeQcML(qcml);

        QcML qcmlNew = new QcML();
        qcmlNew.setFileName("new.qcml");
        qcml.addRunQuality(new QualityAssessment("run_1", false));

        writer.writeQcML(qcmlNew);
    }

    @Test
    public void writeQcML_duplicateSetQuality() {
        QcMLFileReader fileReader = new QcMLFileReader();
        QcML qcml = fileReader.getQcML(loadResource("/CvParameterTest.qcML").getAbsolutePath());
        writer.writeQcML(qcml);

        QcML qcmlNew = new QcML();
        qcmlNew.setFileName("new.qcml");
        qcml.addSetQuality(new QualityAssessment("set_1", true));

        writer.writeQcML(qcmlNew);
    }

    private File loadResource(String fileName) {
        try {
            return new File(getClass().getResource(fileName).toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
        return null;
    }
}
