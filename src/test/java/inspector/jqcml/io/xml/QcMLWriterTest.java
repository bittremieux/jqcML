package inspector.jqcml.io.xml;

import java.io.File;

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.Cv;

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
}
