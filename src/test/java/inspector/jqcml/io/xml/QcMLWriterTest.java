package inspector.jqcml.io.xml;

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

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QcMLWriterTest {

    private QcMLWriter writer;

    @Before
    public void setUp() {
        writer = new QcMLFileWriter();
    }

    @After
    public void tearDown() {
        File file = new File("Invalid.qcML");
        file.delete();
        file = new File("Invalid_version.qcML");
        file.delete();
        file = new File("Null_version.qcML");
        file.delete();
    }

    @Test(expected = NullPointerException.class)
    public void writeQcML_null() {
        writer.writeQcML(null);
    }

    @Test(expected = NullPointerException.class)
    public void writeQcML_nullFileName() {
        QcML qcml = new QcML();
        qcml.setFileName(null);

        Cv cv = new Cv("name", "uri", "cv");
        qcml.addCv(cv);

        QualityAssessment run = new QualityAssessment("run", false);
        QualityParameter param = new QualityParameter("name", cv, "accession", "param");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        writer.writeQcML(qcml);
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

    @Test(expected = UnsupportedOperationException.class)
    public void writeCv_null() {
        writer.writeCv(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void writeCv_valid() {
        writer.writeCv(new Cv("name", "uri", "id"));
    }

    @Test(expected = IllegalStateException.class)
    public void writeQcML_duplicateID() {
        QcML qcml = new QcML();
        qcml.setFileName("Invalid.qcML");

        Cv cv = new Cv("name", "uri", "id");
        qcml.addCv(cv);

        QualityAssessment run = new QualityAssessment("id", false);
        QualityParameter param = new QualityParameter("name", cv, "accession", "id");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        writer.writeQcML(qcml);
    }

    @Test(expected = IllegalStateException.class)
    public void writeQcML_missingContent() {
        QcML qcml = new QcML();
        qcml.setFileName("Invalid.qcML");

        QualityAssessment run = new QualityAssessment("run", false);
        QualityParameter param = new QualityParameter("name", new Cv("name", "uri", "cv"), "accession", "param");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        writer.writeQcML(qcml);
    }
}
