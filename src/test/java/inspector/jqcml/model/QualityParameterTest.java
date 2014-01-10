package inspector.jqcml.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class QualityParameterTest {
	
	private Cv cv;
	private QualityParameter qp;
	
	@Before
	public void setUp() {
		cv = new Cv("my controlled vocabulary", "my/controlled/vocabulary/cv", "cv");
		
		qp = new QualityParameter();
		
		qp.setName("qp_name");
		qp.setValue("qp_value");
		qp.setUnitAccession("qp_unit_accession");
		qp.setUnitName("qp_unit_name");
		qp.setUnitCvRef(cv);
		qp.setAccession("qp_accession");
		qp.setCvRef(cv);
		qp.setId("qp_id");
		qp.setFlag(true);
		
		for(int i = 0; i < 2; i++) {
			Threshold thr = new Threshold("threshold_" + i, cv);
			thr.setFileName("thresholdFileName" + i + ".txt");
			thr.setAccession("thr_accession_" + i);
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
		
		Threshold thr = new Threshold("new threshold", cv);
		thr.setAccession("new_thr_accession");
		qp.addThreshold(thr);
		
		assertEquals(3, qp.getNumberOfThresholds());
		
		Threshold thrOther = new Threshold("some other threshold", cv);
		thrOther.setAccession("other_thr_accession");
		qp.addThreshold(thrOther);

		assertEquals(4, qp.getNumberOfThresholds());
	}
	
	@Test
	public void addThreshold_duplicate() {
		assertEquals(2, qp.getNumberOfThresholds());
		assertEquals("threshold_1", qp.getThreshold("thr_accession_1").getName());
		
		Threshold thr = new Threshold("new threshold, same accession", cv);
		thr.setAccession("thr_accession_1");
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
