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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QualityParameterTest {

    private Cv cv;
    private QualityParameter qp;

    @Before
    public void setUp() {
        cv = new Cv("my controlled vocabulary", "my/controlled/vocabulary/cv", "cv");

        qp = new QualityParameter("qp_name",cv, "qp_accession", "qp_id");

        qp.setValue("qp_value");
        qp.setUnitAccession("qp_unit_accession");
        qp.setUnitName("qp_unit_name");
        qp.setUnitCvRef(cv);
        qp.setFlag(true);

        for(int i = 0; i < 2; i++) {
            Threshold thr = new Threshold("threshold_" + i, cv, "thr_accession_" + i);
            thr.setFileName("thresholdFileName" + i + ".txt");
            qp.addThreshold(thr);
        }
    }

    @Test
    public void getThreshold_null() {
        assertNull(qp.getThreshold(null));
    }

    @Test
    public void getThreshold_nonExisting() {
        assertNull(qp.getThreshold("non-existing threshold"));
    }

    @Test
    public void getThreshold_valid() {
        assertEquals("threshold_0", qp.getThreshold("thr_accession_0").getName());
    }

    @Test(expected=NullPointerException.class)
    public void addThreshold_null() {
        qp.addThreshold(null);
    }

    @Test
    public void addThreshold_new() {
        assertEquals(2, qp.getNumberOfThresholds());

        Threshold thr = new Threshold("new threshold", cv, "new_thr_accession");
        qp.addThreshold(thr);

        assertEquals(3, qp.getNumberOfThresholds());

        Threshold thrOther = new Threshold("some other threshold", cv, "other_thr_accession");
        qp.addThreshold(thrOther);

        assertEquals(4, qp.getNumberOfThresholds());
    }

    @Test
    public void addThreshold_duplicate() {
        assertEquals(2, qp.getNumberOfThresholds());
        assertEquals("threshold_1", qp.getThreshold("thr_accession_1").getName());

        Threshold thr = new Threshold("new threshold, same accession", cv, "thr_accession_1");
        qp.addThreshold(thr);

        assertEquals(2, qp.getNumberOfThresholds());
        assertEquals("new threshold, same accession", qp.getThreshold("thr_accession_1").getName());
    }

    @Test
    public void removeTreshold_null() {
        assertEquals(2, qp.getNumberOfThresholds());

        qp.removeThreshold(null);

        assertEquals(2, qp.getNumberOfThresholds());
    }

    @Test
    public void removeTreshold_nonExisting() {
        assertEquals(2, qp.getNumberOfThresholds());

        qp.removeThreshold("non-existing threshold");

        assertEquals(2, qp.getNumberOfThresholds());
    }

    @Test
    public void removeTreshold_valid() {
        assertEquals(2, qp.getNumberOfThresholds());

        qp.removeThreshold("thr_accession_0");

        assertEquals(1, qp.getNumberOfThresholds());
    }

    @Test
    public void removeAllThresholds() {
        assertEquals(2, qp.getNumberOfThresholds());

        qp.removeAllThresholds();

        assertEquals(0, qp.getNumberOfThresholds());
    }


}
