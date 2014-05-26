package inspector.jqcml.model;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Iterator;

import javax.xml.bind.DatatypeConverter;

import inspector.jqcml.io.QcMLReader;
import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.io.xml.QcMLFileReader;
import inspector.jqcml.io.xml.QcMLFileWriter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;

import org.junit.Before;
import org.junit.Test;

public class QcMLTest {
	
	private QcML qcmlExpected;
	private QcML qcml;
	
	@Before
	public void setUp() {
		// constructed qcML
		SecureRandom random = new SecureRandom();
		qcmlExpected = new QcML();
		// add Cv's
		for(int i = 0; i < 5; i++) {
			Cv cv = new Cv();
			cv.setFullName("cv item " + i);
			cv.setUri("/path/to/cv/" + i);
			cv.setId("cv_" + i);
			qcmlExpected.addCv(cv);
		}
		// add RunQuality's
		for(int i = 0; i < 5; i++) {
			QualityAssessment runQuality = new QualityAssessment("run_" + i);
			// add MetaDataParameters
			for(int j = 0; j < (i+1) * 2; j++) {
				MetaDataParameter param = new MetaDataParameter();
				param.setName("metadata parameter " + i + "-" + j);
				param.setId("mp_run" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setValue(Integer.toString((int)(Math.random()*1000)));
				runQuality.addMetaDataParameter(param);
			}
			// add QualityParameters
			for(int j = 0; j < (i+1) * 3; j++) {
				QualityParameter param = new QualityParameter();
				param.setName("quality parameter " + i + "-" + j);
				param.setId("qp_run" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setValue(Integer.toString((int)(Math.random()*1000)));
				runQuality.addQualityParameter(param);
			}
			// add AttachmentParameters
			for(int j = 0; j < (i+1) * 2; j++) {
				AttachmentParameter param = new AttachmentParameter();
				param.setName("attachment parameter " + i + "-" + j);
				param.setId("ap_run" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setQualityParameterRef(runQuality.getQualityParameter("qp_run" + i + "_param" + j));
				param.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1024, random).toByteArray()));
				runQuality.addAttachmentParameter(param);
			}
			qcmlExpected.addRunQuality(runQuality);
		}
		// add SetQuality's
		for(int i = 0; i < 2; i++) {
			QualityAssessment setQuality = new QualityAssessment("set_" + i);
			// add MetaDataParameters
			for(int j = 0; j < (i+1) * 2; j++) {
				MetaDataParameter param = new MetaDataParameter();
				param.setName("metadata parameter " + i + "-" + j);
				param.setId("mp_set" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setValue(Integer.toString((int)(Math.random()*1000)));
				setQuality.addMetaDataParameter(param);
			}
			// add QualityParameters
			for(int j = 0; j < (i+1) * 5; j++) {
				QualityParameter param = new QualityParameter();
				param.setName("quality parameter " + i + "-" + j);
				param.setId("qp_set" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setValue(Integer.toString((int)(Math.random()*1000)));
				setQuality.addQualityParameter(param);
			}
			// add AttachmentParameters
			for(int j = 0; j < (i+1) * 3; j++) {
				AttachmentParameter param = new AttachmentParameter();
				param.setName("attachment parameter " + i + "-" + j);
				param.setId("ap_set" + i + "_param" + j);
				param.setCvRef(qcmlExpected.getCv("cv_" + (i+j) % qcmlExpected.getNumberOfCvs()));
				param.setAccession("QC:00000" + j);
				param.setQualityParameterRef(setQuality.getQualityParameter("qp_set" + i + "_param" + j));
				param.setBinary(DatatypeConverter.printBase64Binary(new BigInteger(1024, random).toByteArray()));
				setQuality.addAttachmentParameter(param);
			}
			qcmlExpected.addSetQuality(setQuality);
		}
		qcmlExpected.addCv(new Cv("cv item 5", "/path/to/cv/5", "cv_5"));
		qcmlExpected.addCv(new Cv("cv item 6", "/path/to/cv/6", "cv_6"));
		qcmlExpected.addCv(new Cv("cv item 7", "/path/to/cv/7", "cv_7"));
		qcmlExpected.addCv(new Cv("cv item 8", "/path/to/cv/8", "cv_8"));
		qcmlExpected.addCv(new Cv("cv item 9", "/path/to/cv/9", "cv_9"));
		qcmlExpected.addCv(new Cv("cv item 10", "/path/to/cv/10", "cv_10"));
		qcmlExpected.addCv(new Cv("cv item 11", "/path/to/cv/11", "cv_11"));
		qcmlExpected.addCv(new Cv("cv item 12", "/path/to/cv/12", "cv_12"));
		
		// read qcML
		qcml = new QcMLFileReader().getQcML(getClass().getResource("/QcMLTest.qcML").getFile());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getFileName() {
		assertThat(qcml.getFileName(), containsString("QcMLTest.qcML"));
	}

	@Test
	public void getVersion() {
		assertEquals(QcMLReader.QCML_VERSION, qcml.getVersion());
	}
	
	@Test
	public void getNumberOfRunQualities() {		
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
	}
	
	@Test
	public void getRunQuality() {
		// existing RunQuality's
		for(Iterator<QualityAssessment> it = qcmlExpected.getRunQualityIterator(); it.hasNext(); ) {
			String rqKey = it.next().getId();
			assertNotNull(qcml.getRunQuality(rqKey));
		}
		// non-existing RunQuality
		assertNull(qcml.getRunQuality("non-existing id"));
		// null RunQuality
		assertNull(qcml.getRunQuality(null));
	}
	
	@Test
	public void addRunQuality_new() {
		int count = qcml.getNumberOfRunQualities();
		// new RunQuality is absent
		assertNull(qcml.getRunQuality("new RunQuality"));
		// add new RunQuality
		qcml.addRunQuality(new QualityAssessment("new RunQuality"));
		// new RunQuality is present
		assertEquals(qcml.getNumberOfRunQualities(), count+1);
		assertNotNull(qcml.getRunQuality("new RunQuality"));
	}
	
	@Test
	public void addRunQuality_duplicate() {
		int count = qcml.getNumberOfRunQualities();
		// new RunQuality is present
		assertNotNull(qcml.getRunQuality("run_2"));
		QualityAssessment old = qcml.getRunQuality("run_2");
		// add new RunQuality
		qcml.addRunQuality(new QualityAssessment("run_2"));
		// new RunQuality is present
		assertEquals(qcml.getNumberOfRunQualities(), count);
		assertNotNull(qcml.getRunQuality("run_2"));
		assertNotSame(old, qcml.getRunQuality("run_2"));
	}
	
	@Test(expected=NullPointerException.class)
	public void addRunQuality_null() {
		qcml.addRunQuality(null);
	}
	
	@Test
	public void removeRunQuality_null() {
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
		
		qcml.removeRunQuality(null);
		
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
	}
	
	@Test
	public void removeRunQuality_nonExisting() {
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
		
		qcml.removeRunQuality("non-existing runQuality");
		
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
	}
	
	@Test
	public void removeRunQuality_valid() {
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
		
		qcml.removeRunQuality("run_2");
		
		assertEquals(qcmlExpected.getNumberOfRunQualities() - 1, qcml.getNumberOfRunQualities());
	}
	
	@Test
	public void removeAllRunQualities() {
		assertEquals(qcmlExpected.getNumberOfRunQualities(), qcml.getNumberOfRunQualities());
		
		qcml.removeAllRunQualities();
		
		assertEquals(0, qcml.getNumberOfRunQualities());
	}
	
	@Test
	public void getNumberOfSetQualities() {		
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
	}
	
	@Test
	public void getSetQuality() {
		// existing SetQuality's
		for(Iterator<QualityAssessment> it = qcmlExpected.getSetQualityIterator(); it.hasNext(); ) {
			String sqKey = it.next().getId();
			assertNotNull(qcml.getSetQuality(sqKey));
		}
		// non-existing SetQuality
		assertNull(qcml.getSetQuality("non-existing id"));
		// null SetQuality
		assertNull(qcml.getSetQuality(null));
	}
	
	@Test
	public void getSetQuality_noSet() {
		qcml = new QcMLFileReader().getQcML(getClass().getResource("/NoSet.qcML").getFile());
		
		assertEquals(qcml.getNumberOfSetQualities(), 0);	

		assertNull(qcml.getSetQuality("set_1"));
		assertNull(qcml.getSetQuality("non-existing id"));
	}
	
	@Test
	public void addSetQuality_new() {
		int count = qcml.getNumberOfSetQualities();
		// new SetQuality is absent
		assertNull(qcml.getSetQuality("new SetQuality"));
		// add new SetQuality
		qcml.addSetQuality(new QualityAssessment("new SetQuality"));
		// new RunQuality is present
		assertEquals(qcml.getNumberOfSetQualities(), count+1);
		assertNotNull(qcml.getSetQuality("new SetQuality"));
	}
	
	@Test
	public void addSetQuality_duplicate() {
		int count = qcml.getNumberOfSetQualities();
		// new SetQuality is present
		assertNotNull(qcml.getSetQuality("set_1"));
		QualityAssessment old = qcml.getSetQuality("set_1");
		// add new SetQuality
		qcml.addSetQuality(new QualityAssessment("set_1"));
		// new SetQuality is present
		assertEquals(qcml.getNumberOfSetQualities(), count);
		assertNotNull(qcml.getSetQuality("set_1"));
		assertNotSame(old, qcml.getSetQuality("set_1"));
	}
	
	@Test(expected=NullPointerException.class)
	public void addSetQuality_null() {
		qcml.addSetQuality(null);
	}
	
	@Test
	public void removeSetQuality_null() {
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
		
		qcml.removeSetQuality(null);
		
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
	}
	
	@Test
	public void removeSetQuality_nonExisting() {
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
		
		qcml.removeRunQuality("non-existing setQuality");
		
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
	}
	
	@Test
	public void removeSetQuality_valid() {
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
		
		qcml.removeSetQuality("set_0");
		
		assertEquals(qcmlExpected.getNumberOfSetQualities() - 1, qcml.getNumberOfSetQualities());
	}
	
	@Test
	public void removeAllSetQualities() {
		assertEquals(qcmlExpected.getNumberOfSetQualities(), qcml.getNumberOfSetQualities());
		
		qcml.removeAllSetQualities();
		
		assertEquals(0, qcml.getNumberOfSetQualities());
	}
	
	@Test
	public void getNumberOfCvs() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
	}
	
	@Test
	public void getCv() {
		// existing Cv's
		for(Iterator<Cv> it = qcmlExpected.getCvIterator(); it.hasNext(); ) {
			String cvKey = it.next().getId();
			assertNotNull(qcml.getCv(cvKey));
		}
		// non-existing Cv
		assertNull(qcml.getCv("non-existing id"));
		// null Cv
		assertNull(qcml.getCv(null));
	}
	
	@Test
	public void addCv_new() {
		int count = qcml.getNumberOfCvs();
		// new Cv is absent
		assertNull(qcml.getCv("new cv"));
		// add new Cv
		qcml.addCv(new Cv("new Cv name", "/path/to/cv/", "new cv"));
		// new Cv is present
		assertEquals(qcml.getNumberOfCvs(), count+1);
		assertNotNull(qcml.getCv("new cv"));
	}
	
	@Test
	public void addCv_duplicate() {
		int count = qcml.getNumberOfCvs();
		// new Cv is present
		assertNotNull(qcml.getCv("cv_3"));
		Cv old = qcml.getCv("cv_3");
		// add new Cv
		qcml.addCv(new Cv("new Cv name", "/path/to/cv/", "cv_3"));
		// new Cv is present
		assertEquals(qcml.getNumberOfCvs(), count);
		assertNotNull(qcml.getCv("cv_3"));
		assertNotSame(old, qcml.getSetQuality("cv_3"));
	}
	
	@Test(expected=NullPointerException.class)
	public void addCv_null() {
		qcml.addCv(null);
	}
	
	@Test
	public void removeCv_null() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv(null);
		
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
	}
	
	@Test
	public void removeCv_nonExisting() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("non-existing cv");
		
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
	}

	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedMetaDataParameter() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());

		qcml.removeCv("cv_12");
	}

	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedQualityParameter() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());

		qcml.removeCv("cv_5");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedQualityParameterUnit() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_6");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedAttachmentParameter() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_7");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedAttachmentParameterUnit() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_8");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedThreshold() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_9");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeCv_referencedThresholdUnit() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_10");
	}
	
	@Test
	public void removeCv_nonReferenced() {
		assertEquals(qcmlExpected.getNumberOfCvs(), qcml.getNumberOfCvs());
		
		qcml.removeCv("cv_11");
		
		assertEquals(qcmlExpected.getNumberOfCvs() - 1, qcml.getNumberOfCvs());		
	}

}
