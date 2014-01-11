package inspector.jqcml.io.xml;

import java.io.File;

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.Cv;

import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QcMLWriterTest {

	private QcMLWriter writer;

	@Before
	public void setUp() {
		writer = new QcMLFileWriter();
	}

	@Test(expected = NullPointerException.class)
	public void writeQcML_null() {
		writer.writeQcML(null);
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

        QualityAssessment run = new QualityAssessment("id");
        QualityParameter param = new QualityParameter("name", cv, "id");
        param.setAccession("accession");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        writer.writeQcML(qcml);
    }

    @Test(expected = IllegalStateException.class)
    public void writeQcML_missingContent() {
        QcML qcml = new QcML();
        qcml.setFileName("Invalid.qcML");

        QualityAssessment run = new QualityAssessment("run");
        QualityParameter param = new QualityParameter("name", new Cv("name", "uri", "cv"), "param");
        param.setAccession("accession");
        run.addQualityParameter(param);
        qcml.addRunQuality(run);

        writer.writeQcML(qcml);
    }
}
