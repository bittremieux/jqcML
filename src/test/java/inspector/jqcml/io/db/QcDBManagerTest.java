package inspector.jqcml.io.db;

import org.junit.Test;

import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.assertNotNull;

public class QcDBManagerTest {

    @Test
    public void createMySQLFactory_valid() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test
    public void createMySQLFactory_ipHost() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("127.0.0.1", "3306", "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test
    public void createMySQLFactory_nullHost() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory(null, "3306", "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test
    public void createMySQLFactory_nullPort() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", null, "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = NullPointerException.class)
    public void createMySQLFactory_nullDatabase() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", null, "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = NullPointerException.class)
    public void createMySQLFactory_nullUser() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "jqcmltest", null, null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createMySQLFactory_invalidHost() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("nonlocalhost", "3306", "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createMySQLFactory_invalidPort() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "123456", "jqcmltest", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createMySQLFactory_invalidDatabase() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "noDb", "root", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createMySQLFactory_invalidUser() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createMySQLFactory("localhost", "3306", "jqcmltest", "noUser", null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }

    @Test
    public void createSQLiteFactory_valid() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createSQLiteFactory(getClass().getResource("/QcDBManagerTest.sqlite").getFile());
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        assertNotNull(reader.getCv(null, "cv_0"));
        // close emf
        emf.close();
    }

    @Test(expected = NullPointerException.class)
    public void createSQLiteFactory_nullPath() {
        // create EMF
        EntityManagerFactory emf = QcDBManagerFactory.createSQLiteFactory(null);
        // test connection
        QcDBReader reader = new QcDBReader(emf);
        reader.getCv("qcML", "cv");
        // close emf
        emf.close();
    }
}
