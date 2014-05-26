package inspector.jqcml.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inspector.jqcml.io.xml.QcMLFileReader;

public class QualityAssessmentTest {
	
	private QcML qcml;
	
	@Before
	public void setUp() {
		qcml = new QcMLFileReader().getQcML(getClass().getResource("/CvParameterTest.qcML").getFile());
	}
	
	@Test
	public void getMetaDataParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getMetaDataParameter(null));
	}
	
	@Test
	public void getMetaDataParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getMetaDataParameter("non-existing parameter"));
	}
	
	@Test
	public void getMetaDataParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals("metadata parameter 0 at run 1", qa.getMetaDataParameter("QC:000000").getName());
	}
	
	@Test(expected=NullPointerException.class)
	public void addMetaDataParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		qa.addMetaDataParameter(null);
	}
	
	@Test
	public void addMetaDataParameter_new() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		
		MetaDataParameter mp = new MetaDataParameter("new parameter", qcml.getCv("cv_0"), "mp_new");
		mp.setAccession("new_mp_accession");
		qa.addMetaDataParameter(mp);
		
		assertEquals(3, qa.getNumberOfMetaDataParameters());

		MetaDataParameter mpOther = new MetaDataParameter("some other parameter", qcml.getCv("cv_1"), "mp_other");
		mpOther.setAccession("other_qp_accession");
		qa.addMetaDataParameter(mpOther);

		assertEquals(4, qa.getNumberOfMetaDataParameters());
	}
	
	@Test
	public void addMetaDataParameter_duplicate() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		assertEquals("metadata parameter 0 at run 1", qa.getMetaDataParameter("QC:000000").getName());
		
		MetaDataParameter mp = new MetaDataParameter("new parameter, same accession", qcml.getCv("cv_1"), "mp_new");
		mp.setAccession("QC:000000");
		qa.addMetaDataParameter(mp);
		
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		assertEquals("new parameter, same accession", qa.getMetaDataParameter("QC:000000").getName());
	}
	
	@Test
	public void removeMetaDataParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		
		qa.removeMetaDataParameter(null);
		
		assertEquals(2, qa.getNumberOfMetaDataParameters());
	}
	
	@Test
	public void removeMetaDataParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		
		qa.removeMetaDataParameter("non-existing parameter");
		
		assertEquals(2, qa.getNumberOfMetaDataParameters());
	}
	
	@Test
	public void removeMetaDataParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		
		qa.removeMetaDataParameter("QC:000000");
		
		assertEquals(1, qa.getNumberOfMetaDataParameters());
	}
	
	@Test
	public void removeAllMetaDataParameters() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(2, qa.getNumberOfMetaDataParameters());
		
		qa.removeAllMetaDataParameters();
		
		assertEquals(0, qa.getNumberOfMetaDataParameters());
	}

	@Test
	public void getQualityParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getQualityParameter(null));
	}

	@Test
	public void getQualityParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getQualityParameter("non-existing parameter"));
	}

	@Test
	public void getQualityParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals("quality parameter 0 at run 1", qa.getQualityParameter("QC:000000").getName());
	}

	@Test(expected=NullPointerException.class)
	public void addQualityParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		qa.addQualityParameter(null);
	}

	@Test
	public void addQualityParameter_new() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());

		QualityParameter qp = new QualityParameter("new parameter", qcml.getCv("cv_0"), "qp_new");
		qp.setAccession("new_qp_accession");
		qa.addQualityParameter(qp);

		assertEquals(5, qa.getNumberOfQualityParameters());

		QualityParameter qpOther = new QualityParameter("some other parameter", qcml.getCv("cv_1"), "qp_other");
		qpOther.setAccession("other_qp_accession");
		qa.addQualityParameter(qpOther);

		assertEquals(6, qa.getNumberOfQualityParameters());
	}

	@Test
	public void addQualityParameter_duplicate() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());
		assertEquals("quality parameter 0 at run 1", qa.getQualityParameter("QC:000000").getName());

		QualityParameter qp = new QualityParameter("new parameter, same accession", qcml.getCv("cv_1"), "qp_new");
		qp.setAccession("QC:000000");
		qa.addQualityParameter(qp);

		assertEquals(4, qa.getNumberOfQualityParameters());
		assertEquals("new parameter, same accession", qa.getQualityParameter("QC:000000").getName());
	}

	@Test
	public void removeQualityParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());

		qa.removeQualityParameter(null);

		assertEquals(4, qa.getNumberOfQualityParameters());
	}

	@Test
	public void removeQualityParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());

		qa.removeQualityParameter("non-existing parameter");

		assertEquals(4, qa.getNumberOfQualityParameters());
	}

	@Test
	public void removeQualityParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());

		qa.removeQualityParameter("QC:000000");

		assertEquals(3, qa.getNumberOfQualityParameters());
	}

	@Test
	public void removeAllQualityParameters() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(4, qa.getNumberOfQualityParameters());

		qa.removeAllQualityParameters();

		assertEquals(0, qa.getNumberOfQualityParameters());
	}
	
	@Test
	public void getAttachmentParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getAttachmentParameter(null));
	}
	
	@Test
	public void getAttachmentParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertNull(qa.getAttachmentParameter("non-existing parameter"));
	}
	
	@Test
	public void getAttachmentParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals("attachment", qa.getAttachmentParameter("QC:000009").getName());
	}
	
	@Test(expected=NullPointerException.class)
	public void addAttachmentParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		qa.addAttachmentParameter(null);
	}
	
	@Test
	public void addAttachmentParameter_new() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		
		AttachmentParameter ap = new AttachmentParameter("new parameter", qcml.getCv("cv_0"), "ap_new");
		ap.setAccession("new_qp_accession");
		qa.addAttachmentParameter(ap);
		
		assertEquals(2, qa.getNumberOfAttachmentParameters());
		
		AttachmentParameter apOther = new AttachmentParameter("some other parameter", qcml.getCv("cv_1"), "ap_other");
		apOther.setAccession("other_ap_accession");
		qa.addAttachmentParameter(apOther);

		assertEquals(3, qa.getNumberOfAttachmentParameters());
	}
	
	@Test
	public void addAttachmentParameter_duplicate() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		assertEquals("attachment", qa.getAttachmentParameter("QC:000009").getName());
		
		AttachmentParameter ap = new AttachmentParameter("new parameter, same accession", qcml.getCv("cv_1"), "ap_new");
		ap.setAccession("QC:000009");
		qa.addAttachmentParameter(ap);
		
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		assertEquals("new parameter, same accession", qa.getAttachmentParameter("QC:000009").getName());
	}
	
	@Test
	public void removeAttachmentParameter_null() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		
		qa.removeAttachmentParameter(null);
		
		assertEquals(1, qa.getNumberOfAttachmentParameters());
	}
	
	@Test
	public void removeAttachmentParameter_nonExisting() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		
		qa.removeAttachmentParameter("non-existing parameter");
		
		assertEquals(1, qa.getNumberOfAttachmentParameters());
	}
	
	@Test
	public void removeAttachmentParameter_valid() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		
		qa.removeAttachmentParameter("QC:000009");
		
		assertEquals(0, qa.getNumberOfAttachmentParameters());
	}
	
	@Test
	public void removeAllAttachmentParameters() {
		QualityAssessment qa = qcml.getRunQuality("run_1");
		assertEquals(1, qa.getNumberOfAttachmentParameters());
		
		qa.removeAllAttachmentParameters();
		
		assertEquals(0, qa.getNumberOfAttachmentParameters());
	}

}
