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
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class QcDBReaderIT {

    private static final String PORT = System.getProperty("mysql.port");

    private EntityManagerFactory emf;
    private QcDBReader reader;

    @Before
    public void setUp() {
        emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
        reader = new QcDBReader(emf);

        QcMLFileReader xmlReader = new QcMLFileReader();
        QcML qcml = xmlReader.getQcML(loadResource("/CvParameterTest.qcML").getAbsolutePath());

        QcDBWriter writer = new QcDBWriter(emf);
        writer.writeQcML(qcml);
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
    public void getCv_nullQcML() {
        assertNotNull(reader.getCv(null, "cv_0"));
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
    public void getCvIterator_nullQcML() {
        Iterator<Cv> it = reader.getCvIterator(null);
        assertTrue(it.hasNext());
    }

    @Test
    public void getQualityAssessment_nonExistingQcML() {
        assertNull(reader.getQualityAssessment("NonExisting.qcML", "id"));
    }

    @Test
    public void getQualityAssessment_nullQcML() {
        assertNotNull(reader.getQualityAssessment(null, "run_1"));
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
    public void getQualityAssessment_valid() {
        assertNotNull(reader.getQualityAssessment("CvParameterTest.qcML", "run_1"));
    }

    @Test
    public void getQualityAssessmentIterator_nonExistingQcML() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator("NonExisting.qcML");
        assertFalse(it.hasNext());
    }

    @Test
    public void getQualityAssessmentIterator_validQcML() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator("CvParameterTest.qcML");
        assertTrue(it.hasNext());
    }

    @Test
    public void getQualityAssessmentIterator_nullQcML() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator(null);
        assertTrue(it.hasNext());
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
