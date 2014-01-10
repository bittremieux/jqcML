package inspector.jqcml.io.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;

public class QcDBWriterTest {

    private EntityManagerFactory emf;
	private QcDBWriter writer;

	@Before
	public void setUp() {
        emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "mydb", "root", null);
		writer = new QcDBWriter(emf);
	}

	@After
	public void tearDown() {
        emf.close();
	}

	@Test(expected = NullPointerException.class)
	public void writeQcML_null() {
		writer.writeQcML(null);
	}

    @Test(expected = NullPointerException.class)
    public void writeCv_null() {
        writer.writeCv(null);
    }
}
