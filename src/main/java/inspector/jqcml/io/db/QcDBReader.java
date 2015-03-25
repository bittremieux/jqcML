package inspector.jqcml.io.db;

import inspector.jqcml.io.QcMLReader;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.queries.CursoredStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;

/**
 * A qcML input reader which takes its input from a qcDB RDBMS.
 */
public class QcDBReader implements QcMLReader {

    private static final Logger LOGGER = LogManager.getLogger(QcDBReader.class);

    /** EntityManagerFactory used to set up connections to the database */
    private EntityManagerFactory factory;

    /**
     * Creates a QcDBReader specified by the given {@link EntityManagerFactory}.
     *
     * @param factory  the EntityManagerFactory used to set up connections to the database
     */
    public QcDBReader(EntityManagerFactory factory) {
        this.factory = factory;
    }

    /**
     * Creates an {@link EntityManager} to set up a connection to the database.
     *
     * @return an EntityManager to connect to the database
     */
    private EntityManager createEntityManager() {
        try {
            return factory.createEntityManager();
        } catch(Exception e) {
            LOGGER.info("Error while creating the EntityManager to connect to the database: {}", e);
            throw new IllegalStateException("Couldn't connect to the database: " + e);
        }
    }

    @Override
    public QcML getQcML(String qcmlFile) {
        LOGGER.info("Retrieve qcML {}", qcmlFile);

        EntityManager entityManager = createEntityManager();

        try {
            TypedQuery<QcML> query = entityManager.createQuery("SELECT qcml FROM QcML qcml WHERE qcml.fileName = :fileName", QcML.class);
            query.setParameter("fileName", qcmlFile);

            QcML qcml = query.getSingleResult();

            if(!qcml.getVersion().equals(QCML_VERSION)) {
                LOGGER.warn("The qcML version <{}> doesn't correspond to the qcML XML schema version <{}>", qcml.getVersion(), QCML_VERSION);
            }

            return qcml;
        } catch(NoResultException e) {
            LOGGER.info("No result found for <{}>", qcmlFile, e);
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Cv getCv(String qcmlFile, String id) {
        LOGGER.info("Retrieve Cv (id={}) from qcML {}", id, qcmlFile);

        EntityManager entityManager = createEntityManager();

        try {
            TypedQuery<Cv> query;
            if(qcmlFile == null) {
                query = entityManager.createQuery("SELECT cv FROM Cv cv WHERE cv.id = :id", Cv.class);
            } else {
                query = entityManager.createQuery("SELECT cv FROM QcML qcml, IN(qcml.cvList) cv " +
                        "WHERE qcml.fileName = :fileName AND cv.id = :id", Cv.class);
                query.setParameter("fileName", qcmlFile);
            }
            query.setParameter("id", id);

            return query.getSingleResult();
        } catch(NoResultException e) {
            LOGGER.info("No cv <{}> found for <{}>", id, qcmlFile, e);
            return null;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Returns an {@link Iterator} over all {@link Cv} objects.
     *
     * If a specific qcML object is specified, the iterator returns all Cv objects referenced by this qcML object.
     * If no qcML object is specified, the iterator returns all Cv objects in the full qcDB.
     *
     * @param qcmlFile  the (optional) qcML identifier by which the Cv objects are referenced
     * @return an iterator over all Cv objects
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Cv> getCvIterator(String qcmlFile) {
        LOGGER.info("Retrieve a CV iterator from qcML {}", qcmlFile);

        EntityManager entityManager = createEntityManager();

        try {
            TypedQuery<?> query;
            if(qcmlFile == null) {
                query = entityManager.createQuery("SELECT cv FROM Cv cv", Cv.class);
            } else {
                query = entityManager.createQuery("SELECT cv FROM QcML qcml, IN(qcml.cvList) cv " +
                        "WHERE qcml.fileName = :fileName", Cv.class);
                query.setParameter("fileName", qcmlFile);
            }

            query.setHint("eclipselink.cursor", true);
            return (CursoredStream) query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public QualityAssessment getQualityAssessment(String qcmlFile, String id) {
        LOGGER.info("Retrieve QualityAssessment (id={}) from qcML {}", id, qcmlFile);

        EntityManager entityManager = createEntityManager();

        try {
            TypedQuery<QualityAssessment> query;
            if(qcmlFile == null) {
                query = entityManager.createQuery("SELECT qa FROM QualityAssessment qa " +
                        "WHERE qa.id = :id", QualityAssessment.class);
            } else {
                // UNION unfortunately doesn't work due to a bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=378573
                /*TypedQuery<QualityAssessment> query = entityManager.createQuery("SELECT qa FROM QcML qcml, IN(qcml.runQuality) qa " +
                        "WHERE qcml.fileName = :fileName AND qa.id = :id " +
                        "UNION " +
                        "SELECT qa FROM QcML qcml, IN(qcml.setQuality) qa " +
                        "WHERE qa.id = :id AND qcml.fileName = :fileName", QualityAssessment.class);*/
                query = entityManager.createQuery("SELECT qa FROM QualityAssessment qa " +
                        "WHERE qa.id = :id AND " +
                        "(qa.id IN (SELECT rq.id FROM QcML qcml, IN(qcml.runQuality) rq WHERE qcml.fileName = :fileName) OR " +
                        "qa.id IN (SELECT sq.id FROM QcML qcml, IN(qcml.setQuality) sq WHERE qcml.fileName = :fileName))", QualityAssessment.class);
                query.setParameter("fileName", qcmlFile);
            }
            query.setParameter("id", id);

            return query.getSingleResult();
        } catch(NoResultException e) {
            LOGGER.info("No quality assessment <{}> found for <{}>", id, qcmlFile, e);
            return null;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Returns an {@link Iterator} over all {@link QualityAssessment} objects.
     *
     * If a specific qcML object is specified, the iterator returns all QualityAssessment objects referenced by this qcML object.
     * If no qcML object is specified, the iterator returns all QualityAssessment objects in the full qcDB.
     *
     * @param qcmlFile  the (optional) qcML identifier by which the QualityAssessment objects are referenced
     * @return an iterator over all QualityAssessment objects
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<QualityAssessment> getQualityAssessmentIterator(String qcmlFile) {
        LOGGER.info("Retrieve a QualityAssessment iterator from qcML {}", qcmlFile);

        EntityManager entityManager = createEntityManager();

        try {
            TypedQuery<?> query;
            if(qcmlFile == null) {
                query = entityManager.createQuery("SELECT qa FROM QualityAssessment qa", QualityAssessment.class);
            } else {
                query = entityManager.createQuery("SELECT qa FROM QualityAssessment qa " +
                        "WHERE qa.id IN (SELECT rq.id FROM QcML qcml, IN(qcml.runQuality) rq WHERE qcml.fileName = :fileName) OR " +
                        "qa.id IN (SELECT sq.id FROM QcML qcml, IN(qcml.setQuality) sq WHERE qcml.fileName = :fileName)", QualityAssessment.class);
                query.setParameter("fileName", qcmlFile);
            }

            query.setHint("eclipselink.cursor", true);
            return (CursoredStream) query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Provides the functionality to retrieve arbitrary data from the qcDB.
     *
     * @param query  the query used to retrieve the data
     * @param clss  the class type of the object returned by the query
     * @param <T>  the type of the requested data
     * @return a List containing all objects of the given type returned by the given query
     */
    public <T> List<T> getFromCustomQuery(String query, Class<T> clss) {
        if(query != null) {
            LOGGER.info("Execute custom query: {}", query);

            EntityManager entityManager = createEntityManager();

            try {
                return entityManager.createQuery(query, clss).getResultList();
            } finally {
                entityManager.close();
            }
        } else {
            LOGGER.error("Unable to execute <null> query");
            throw new NullPointerException("Unable to execute <null> query");
        }
    }

}
