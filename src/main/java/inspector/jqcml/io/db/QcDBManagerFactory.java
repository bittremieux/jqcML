package inspector.jqcml.io.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates an {@link EntityManagerFactory} with specific properties to connect to a certain type of database.
 * 
 * Remark: Don't forget to {@code close} the EntityManagerFactory after it has been fully used.
 */
public class QcDBManagerFactory {

    private static final Logger LOGGER = LogManager.getLogger(QcDBManagerFactory.class);

    private QcDBManagerFactory() {

    }

    /**
     * Adds general (non-database specific) properties to the properties Map used to set up the database connection.
     * Specifically some EclipseLink properties are set.
     */
    private static void addGeneralProperties(Map<String, String> properties) {
        // make sure the required tables are generated if they aren't present
        properties.put("eclipselink.ddl-generation", "create-or-extend-tables");
        // execute the table generation on the database
        properties.put("eclipselink.ddl-generation.output-mode", "database");
        // http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging#Log_Levels
        properties.put("eclipselink.logging.level", "CONFIG");
        properties.put("eclipselink.logging.file", "jpa.log");
    }

    /**
     * Creates an {@link EntityManagerFactory} for a MySQL database.
     *
     * @param host  the MySQL host. If {@code null}, localhost is used as default.
     * @param port  the MySQL port. If {@code null}, 3306 is used as default.
     * @param db  the MySQL database schema
     * @param user  the MySQL user name
     * @param password  the MySQL password. If <code>null</code>, no password is used.
     * @return an EntityManagerFactory to be used to connect to the specified database
     */
    public static EntityManagerFactory createMySQLFactory(String host, String port, String db, String user, String password) {
        if(db == null) {
            throw new NullPointerException("Invalid database schema");
        }
        if(user == null) {
            throw new NullPointerException("Invalid database user");
        }

        LOGGER.info("Create MySQL EntityManagerFactory");

        Map<String, String> properties = new HashMap<>();

        // add general properties
        addGeneralProperties(properties);

        // add specific properties
        properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + (host == null ? "localhost" : host) + ":" + (port == null ? "3306" : port) + "/" + db;
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.user", user);
        if(password != null) {
            properties.put("javax.persistence.jdbc.password", password);
        }

        // create entity manager
        return Persistence.createEntityManagerFactory("jqcML", properties);
    }

    /**
     * Creates an {@link EntityManagerFactory} for a SQLite database.
     *
     * @param dbPath  path to the SQLite database file
     * @return an EntityManagerFactory to be used to connect to the specified database
     */
    public static EntityManagerFactory createSQLiteFactory(String dbPath) {
        if(dbPath == null) {
            throw new NullPointerException("Invalid database path");
        }

        LOGGER.info("Create SQLite EntityManagerFactory");

        Map<String, String> properties = new HashMap<>();

        // add general properties
        addGeneralProperties(properties);

        // add specific properties
        properties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
        String url = "jdbc:sqlite:" + dbPath;
        properties.put("javax.persistence.jdbc.url", url);

        // create entity manager
        return Persistence.createEntityManagerFactory("jqcML", properties);
    }
}
