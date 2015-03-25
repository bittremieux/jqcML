package inspector.jqcml.io.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;

public class QcDBWriterIT {

    private static final String PORT = System.getProperty("mysql.port");

    private EntityManagerFactory emf;
	private QcDBWriter writer;

	@Before
	public void setUp() {
        emf = QcDBManagerFactory.createMySQLFactory("localhost", PORT, "root", "root", "root");
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
