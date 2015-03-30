package inspector.jqcml.model;

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
