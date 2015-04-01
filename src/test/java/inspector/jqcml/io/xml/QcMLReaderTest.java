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

import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.junit.Assert.*;

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
        reader.validate("/NonExisting.qcML");
    }

    @Test
    public void validate_noXMLFile() {
        assertFalse(reader.validate(loadResource("/PlainText.qcML").getAbsolutePath()));
    }

    @Test
    public void validate_invalidXMLFile() {
        assertFalse(reader.validate(loadResource("/Invalid.qcML").getAbsolutePath()));
    }

    @Test
    public void validate_validQcMLFile() {
        assertTrue(reader.validate(loadResource("/CvParameterTest.qcML").getAbsolutePath()));
    }

    @Test(expected=NullPointerException.class)
    public void getQcML_null() {
        reader.getQcML(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQcML_nonExistingFile() {
        reader.getQcML("/NonExisting.qcML");
    }

    @Test
    public void getQcML_noXMLFile() {
        assertNull(reader.getQcML(loadResource("/PlainText.qcML").getAbsolutePath()));
    }

    @Test
    public void getQcML_invalidXMLFile() {
        assertNull(reader.getQcML(loadResource("/Invalid.qcML").getAbsolutePath()));
    }

    @Test
    public void getQcML_invalidVersionFormat() {
        // warning should be logged
        assertNull(reader.getQcML(loadResource("/InvalidVersionFormat.qcML").getAbsolutePath()));
    }

    @Test
    public void getQcML_invalidVersionNumber() {
        // warning should be logged
        QcML qcml = reader.getQcML(loadResource("/InvalidVersionNumber.qcML").getAbsolutePath());

        assertNotNull(qcml);

        assertEquals(QcMLFileReader.QCML_VERSION, qcml.getVersion());
    }

    @Test(expected=NullPointerException.class)
    public void getCv_nullFile() {
        reader.getCv(null, "id");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getCv_nonExistingFile() {
        reader.getCv("/NonExisting.qcML", "id");
    }

    @Test
    public void getCv_noXMLFile() {
        assertNull(reader.getCv(loadResource("/PlainText.qcML").getAbsolutePath(), "id"));
    }

    @Test
    public void getCv_invalidXMLFile() {
        assertNull(reader.getCv(loadResource("/Invalid.qcML").getAbsolutePath(), "id"));
    }

    @Test
    public void getCv_nullId() {
        assertNull(reader.getCv(loadResource("/CvParameterTest.qcML").getAbsolutePath(), null));
    }

    @Test
    public void getCv_nonExistingId() {
        assertNull(reader.getCv(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "non-existing id"));
    }

    @Test
    public void getCv_valid() {
        assertNotNull(reader.getCv(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "cv_0"));
        assertNotNull(reader.getCv(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "cv_1"));
    }

    @Test(expected=NullPointerException.class)
    public void getCvIterator_nullFile() {
        reader.getCvIterator(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getCvIterator_nonExistingFile() {
        reader.getCvIterator("/NonExisting.qcML");
    }

    @Test
    public void getCvIterator_noXMLFile() {
        Iterator<Cv> it = reader.getCvIterator(loadResource("/PlainText.qcML").getAbsolutePath());

        assertFalse(it.hasNext());
    }

    @Test
    public void getCvIterator_invalidXMLFile() {
        Iterator<Cv> it = reader.getCvIterator(loadResource("/Invalid.qcML").getAbsolutePath());

        assertFalse(it.hasNext());
    }

    @Test
    public void getCvIterator_valid() {
        Iterator<Cv> it = reader.getCvIterator(loadResource("/CvParameterTest.qcML").getAbsolutePath());

        for(int i = 0; i < 2; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    @Test(expected=NullPointerException.class)
    public void getQualityAssessment_nullFile() {
        reader.getQualityAssessment(null, "id");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQualityAssessment_nonExistingFile() {
        reader.getQualityAssessment("/NonExisting.qcML", "id");
    }

    @Test
    public void getQualityAssessment_noXMLFile() {
        assertNull(reader.getQualityAssessment(loadResource("/PlainText.qcML").getAbsolutePath(), "id"));
    }

    @Test
    public void getQualityAssessment_invalidXMLFile() {
        assertNull(reader.getQualityAssessment(loadResource("/Invalid.qcML").getAbsolutePath(), "id"));
    }

    @Test
    public void getQualityAssessment_nullId() {
        assertNull(reader.getQualityAssessment(loadResource("/CvParameterTest.qcML").getAbsolutePath(), null));
    }

    @Test
    public void getQualityAssessment_nonExistingId() {
        assertNull(reader.getQualityAssessment(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "non-existing id"));
    }

    @Test
    public void getQualityAssessment_valid() {
        assertNotNull(reader.getQualityAssessment(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "run_1"));
        assertNotNull(reader.getQualityAssessment(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "run_2"));
        assertNotNull(reader.getQualityAssessment(loadResource("/CvParameterTest.qcML").getAbsolutePath(), "set_1"));
    }

    @Test(expected=NullPointerException.class)
    public void getQualityAssessmentIterator_nullFile() {
        reader.getQualityAssessmentIterator(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getQualityAssessmentIterator_nonExistingFile() {
        reader.getQualityAssessmentIterator("/NonExisting.qcML");
    }

    @Test
    public void getQualityAssessmentIterator_noXMLFile() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator(loadResource("/PlainText.qcML").getAbsolutePath());

        assertFalse(it.hasNext());
    }

    @Test
    public void getQualityAssessmentIterator_invalidXMLFile() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator(loadResource("/Invalid.qcML").getAbsolutePath());

        assertFalse(it.hasNext());
    }

    @Test
    public void getQualityAssessmentIterator_valid() {
        Iterator<QualityAssessment> it = reader.getQualityAssessmentIterator(loadResource("/CvParameterTest.qcML").getAbsolutePath());

        for(int i = 0; i < 3; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
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
