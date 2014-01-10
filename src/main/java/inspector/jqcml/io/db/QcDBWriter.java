package inspector.jqcml.io.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inspector.jqcml.io.QcMLWriter;
import inspector.jqcml.model.AttachmentParameter;
import inspector.jqcml.model.Cv;
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;
import inspector.jqcml.model.QualityParameter;
import inspector.jqcml.model.Threshold;

/**
 * A qcML output writer which writes to a qcDB RDBMS.
 */
public class QcDBWriter implements QcMLWriter {
	
	private static final Logger logger = LogManager.getLogger(QcDBWriter.class);

    /** EntityManagerFactory used to set up connections to the database */
	private EntityManagerFactory factory;
	
	/**
	 * Creates a QcDBWriter specified by the given {@link EntityManagerFactory}.
	 */
	public QcDBWriter(EntityManagerFactory factory) {
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
        }
        catch(Exception e) {
            logger.info("Error while creating the EntityManager to connect to the database: {}", e);
            throw new IllegalStateException("Couldn't connect to the database: " + e);
        }
    }

	/**
	 * When writing a qcML object to a qcDB, not all child elements are processed equivalently in case of elements with a duplicate id:
	 * - QualityAssessments (runQuality and setQuality) are fully replaced by the new value
	 * - CVTypes are not replaced, instead the old value is retained and the references stay valid
	 * 
	 * Elements with a unique id are added to the qcDB without any special behavior.
	 */
	@Override
	public void writeQcML(QcML qcml) {
        if(qcml != null) {
            logger.info("Store qcML <{}>", qcml.getFileName());

            EntityManager entityManager = createEntityManager();

            // persist the qcML object in a transaction
            try {
                // check if the qcML object is already in the database and if so, delete the old information
                // (the delete will cascade to the child elements, except for Cv's)
                TypedQuery<Integer> qcmlQuery = entityManager.createQuery("SELECT qcml.primaryKey FROM QcML qcml WHERE qcml.fileName = :fileName", Integer.class);
                qcmlQuery.setParameter("fileName", qcml.getFileName());
                qcmlQuery.setMaxResults(1);	// restrict to a single result (unique index on fileName anyway)

                List<Integer> result = qcmlQuery.getResultList();
                if(result.size() > 0) {
                    // delete the old qcML
                    logger.info("Duplicate qcML <id={}>: delete old qcML <primaryKey={}>", qcml.getFileName(), result.get(0));
                    QcML oldQcml = entityManager.find(QcML.class, result.get(0));
                    entityManager.getTransaction().begin();
                    entityManager.remove(oldQcml);
                    entityManager.getTransaction().commit();
                }

                // delete duplicate QA's
                // dynamically build the query because otherwise we can have a problem with IN and an empty collection
                StringBuilder querySB = new StringBuilder();
                querySB.append("SELECT NEW inspector.jqcml.io.db.IdKeyPair(qa.id, qa.primaryKey) FROM QualityAssessment qa");
                if(qcml.getNumberOfRunQualities() > 0) {
                    querySB.append(" WHERE qa.id IN :rqId");
                    if(qcml.getNumberOfSetQualities() > 0)
                        querySB.append(" OR qa.id IN :sqId");
                }
                else if(qcml.getNumberOfSetQualities() > 0)
                    querySB.append(" WHERE qa.id IN :sqId");
                TypedQuery<IdKeyPair> qaQuery = entityManager.createQuery(querySB.toString(), IdKeyPair.class);
                // try to fill in the parameters
                try {
                    ArrayList<String> rqId = new ArrayList<>(qcml.getNumberOfRunQualities());
                    for(Iterator<QualityAssessment> it = qcml.getRunQualityIterator(); it.hasNext(); )
                        rqId.add(it.next().getId());
                    qaQuery.setParameter("rqId", rqId);
                    ArrayList<String> sqId = new ArrayList<>(qcml.getNumberOfSetQualities());
                    for(Iterator<QualityAssessment> it = qcml.getSetQualityIterator(); it.hasNext(); )
                        sqId.add(it.next().getId());
                    qaQuery.setParameter("sqId", sqId);
                }
                catch (IllegalArgumentException iae) {
                    // expected exception if parameter :rqId or :sqId isn't present
                }
                Map<String, Integer> qaIds = getIdMap(qaQuery);
                if(qaIds.size() > 0) {	// only start a transaction if required
                    entityManager.getTransaction().begin();
                    // delete each duplicate QA
                    for(Entry<String, Integer> entry : qaIds.entrySet()) {
                        logger.info("Duplicate QA <id={}>: delete old QA <primaryKey={}>", entry.getKey(), entry.getValue());
                        QualityAssessment qa = entityManager.find(QualityAssessment.class, entry.getValue());
                        entityManager.remove(qa);
                    }
                    entityManager.getTransaction().commit();
                }

                // replace duplicate Cv's
                TypedQuery<IdKeyPair> cvQuery = entityManager.createQuery("SELECT NEW inspector.jqcml.io.db.IdKeyPair(cv.id, cv.primaryKey) FROM Cv cv", IdKeyPair.class);
                Map<String, Integer> cvIds = getIdMap(cvQuery);
                // check the id's and apply the primary keys in case of a duplication
                for(Iterator<Cv> it = qcml.getCvIterator(); it.hasNext(); ) {
                    Cv cv = it.next();
                    Integer key = cvIds.get(cv.getId());
                    if(key != null) {
                        logger.info("Duplicate CV <id={}>: assign primary key <{}>", cv.getId(), key);
                        cv.setPrimaryKey(key);
                    }
                }

                // store the new qcML
                entityManager.getTransaction().begin();
                entityManager.merge(qcml);
                entityManager.getTransaction().commit();
            }
            catch(EntityExistsException e) {
                try {
                    entityManager.getTransaction().rollback();
                }
                catch(PersistenceException p) {
                    logger.error("Unable to rollback the transaction for <{}> to the qcDB: {}", qcml.getFileName(), p);
                }

                logger.error("Unable to persist <{}> to the qcDB: {}", qcml.getFileName(), e);
                throw new IllegalArgumentException("Unable to persist <" + qcml.getFileName() + "> to the qcDB");
            }
            catch(RollbackException e) {
                logger.error("Unable to commit the transaction for <{}> to the qcDB: {}", qcml.getFileName(), e);
                throw new IllegalArgumentException("Unable to commit the transaction for <" + qcml.getFileName() + "> to the qcDB");
            }
            finally {
                entityManager.close();
            }
        }
        else {
            logger.error("Unable to write <null> qcML element to the qcDB");
            throw new NullPointerException("Unable to write <null> qcML element to the qcDB");
        }
	}

	@Override
	public void writeCv(Cv cv) {
        if(cv != null) {
            logger.info("Store cv <id={}>", cv.getId());

            EntityManager entityManager = createEntityManager();

            // persist the Cv in a transaction
            try {
                // check if the Cv object is already in the database and retrieve its primary key
                TypedQuery<Integer> query = entityManager.createQuery("SELECT cv.primaryKey FROM Cv cv WHERE cv.id = :id", Integer.class);
                query.setParameter("id", cv.getId()).setMaxResults(1);
                List<Integer> result = query.getResultList();
                if(result.size() > 0) {
                    logger.info("Duplicate cv <id={}>: assign primary key <{}>", cv.getId(), result.get(0));
                    cv.setPrimaryKey(result.get(0));
                }

                // store this Cv object
                entityManager.getTransaction().begin();
                entityManager.merge(cv);
                entityManager.getTransaction().commit();
            }
            catch(EntityExistsException e) {
                try {
                    entityManager.getTransaction().rollback();
                }
                catch(PersistenceException p) {
                    logger.error("Unable to rollback the transaction for cv <id={}> to the qcDB: {}", cv.getId(), p);
                }

                logger.error("Unable to persist cv <id={}> to the qcDB: {}", cv.getId(), e);
                throw new IllegalArgumentException("Unable to persist cv <id=" + cv.getId() + "> to the qcDB");
            }
            catch(RollbackException e) {
                logger.error("Unable to commit the transaction for cv <id={}> to the qcDB: {}", cv.getId(), e);
                throw new IllegalArgumentException("Unable to commit the transaction for cv <id=" + cv.getId() + "> to the qcDB");
            }
            finally {
                entityManager.close();
            }
        }

        else {
            logger.error("Unable to write <null> cv element to the qcDB");
            throw new NullPointerException("Unable to write <null> cv element to the qcDB");
        }
	}

	// This method has been made unavailable because the DB constraints
	// enforce a valid foreign key to a qcML object for each QualityAssessment.
	// Therefore, the valid execution of this method can't be guaranteed.
	/*@Override
	public void writeQualityAssessment(QualityAssessment qa) {
		LOGGER.info("Store QualityAssessment <id={}>", qa.getId());
		
		EntityManager entityManager = factory.createEntityManager();
		
		// persist the QualityAssessment in a transaction
		try {
			// check if the QA is already in the database and if so, delete the old information
			// (the delete will cascade to the child elements)
			TypedQuery<Integer> query = entityManager.createQuery("SELECT qa.primaryKey FROM QualityAssessment qa WHERE qa.id = :id", Integer.class);
			query.setParameter("id", qa.getId());
			query.setMaxResults(1);	// restrict to a single result (unique index on id anyway)
			
			List<Integer> result = query.getResultList();
			if(result.size() > 0) {
				// delete the old QA
				LOGGER.info("Duplicate QualityAssessment <id={}>: delete old QualityAssessment <primaryKey={}>", qa.getId(), result.get(0));
				QualityAssessment oldQA = entityManager.find(QualityAssessment.class, result.get(0));
				entityManager.getTransaction().begin();
				entityManager.remove(oldQA);
				entityManager.getTransaction().commit();
			}

			// check whether the cv-refs in the parameters refer to existing Cv's
			TypedQuery<IdKeyPair> cvQuery = entityManager.createQuery("SELECT NEW inspector.jqcml.io.db.IdKeyPair(cv.id, cv.primaryKey) FROM CVType cv", IdKeyPair.class);
			Map<String, Integer> cvIds = getIdMap(entityManager, cvQuery);
			mergeCv(cvIds, qa);
			
			// temporarily remove the parent relationship to avoid cascades
			QcML parent = qa.getParent();
			qa.setParent(null);
			
			// store this QualityAssessment object
			entityManager.getTransaction().begin();
			entityManager.merge(qa);
			entityManager.getTransaction().commit();
			
			// restore the parent relationship
			qa.setParent(parent);
		}
		catch(EntityExistsException e) {
			try {
				entityManager.getTransaction().rollback();
			}
			catch(PersistenceException p) {
				LOGGER.error("Unable to rollback the transaction for QualityAssessment <id={}> to the qcDB: {}", qa.getId(), p);
			}
			
			LOGGER.error("Unable to persist QualityAssessment <id={}> to the qcDB: {}", qa.getId(), e);
			throw new IllegalArgumentException("Unable to persist QualityAssessment <id=" + qa.getId() + "> to the qcDB");
		}
		catch(RollbackException e) {
			LOGGER.error("Unable to commit the transaction for QualityAssessment <id={}> to the qcDB: {}", qa.getId(), e);
			throw new IllegalArgumentException("Unable to commit the transaction for QualityAssessment <id=" + qa.getId() + "> to the qcDB");
		}
		finally {
			entityManager.close();
		}
	}*/
	
	/**
	 * Fill in the primary keys for all cvRefs and unitCvRefs referenced in the child elements of the given QualityAssessment.
	 * 
	 * @param cvIds  map containing all ID-primary key pairs for the Cv's
	 * @param qa  the QualityAssessment for which the primary keys of the (unit) cv refs in the child elements will be checked
	 */
	private void mergeCv(Map<String, Integer> cvIds, QualityAssessment qa) {
		// check all QualityParameters in this QualityAssessment
		for(Iterator<QualityParameter> it = qa.getQualityParameterIterator(); it.hasNext(); ) {
			QualityParameter qp = it.next();
			if(qp.getCvRef() != null && qp.getCvRef().getPrimaryKey() == 0) {
				Integer key = cvIds.get(qp.getCvRef().getId());
				if(key != null)
					qp.getCvRef().setPrimaryKey(key);
			}
			if(qp.getUnitCvRef() != null && qp.getUnitCvRef().getPrimaryKey() == 0) {
				Integer key = cvIds.get(qp.getUnitCvRef().getId());
				if(key != null)
					qp.getUnitCvRef().setPrimaryKey(key);
			}
			
			// check all Thresholds
			for(Iterator<Threshold> thresholdIt = qp.getThresholdIterator(); thresholdIt.hasNext(); ) {
				Threshold threshold = thresholdIt.next();
				if(threshold.getCvRef() != null && threshold.getCvRef().getPrimaryKey() == 0) {
					Integer key = cvIds.get(threshold.getCvRef().getId());
					if(key != null)
						threshold.getCvRef().setPrimaryKey(key);
				}
				if(threshold.getUnitCvRef() != null && threshold.getUnitCvRef().getPrimaryKey() == 0) {
					Integer key = cvIds.get(threshold.getUnitCvRef().getId());
					if(key != null)
						threshold.getUnitCvRef().setPrimaryKey(key);
				}
			}
		}
		
		// check all AttachmentParameters in this QualityAssessment
		for(Iterator<AttachmentParameter> it = qa.getAttachmentParameterIterator(); it.hasNext(); ) {
			AttachmentParameter ap = it.next();
			if(ap.getCvRef() != null && ap.getCvRef().getPrimaryKey() == 0) {
				Integer key = cvIds.get(ap.getCvRef().getId());
				if(key != null)
					ap.getCvRef().setPrimaryKey(key);
			}
			if(ap.getUnitCvRef() != null && ap.getUnitCvRef().getPrimaryKey() == 0) {
				Integer key = cvIds.get(ap.getUnitCvRef().getId());
				if(key != null)
					ap.getUnitCvRef().setPrimaryKey(key);
			}
		}
	}

	/**
	 * Retrieve the combination of all ID's and primary keys for a specific table.
	 * The given query should have as a result an {@link IdKeyPair} for the required table.
	 *
	 * @param idQuery  the query to retrieve all ID's and primary keys. The result of the query should be of the type {@link IdKeyPair}, representing an ID as a String, and a primary key as an integer.
	 * @return a Map consisting of the ID's as the key, and the corresponding primary key as the value
	 */
	private Map<String, Integer> getIdMap(TypedQuery<IdKeyPair> idQuery) {
		// execute the query to retrieve the id's and the primary keys
		List<IdKeyPair> idList = idQuery.getResultList();
		Map<String, Integer> idMap = new HashMap<>();
		// copy id's into a HashMap for easy retrieval
		for(IdKeyPair id : idList)
			idMap.put(id.getId(), id.getKey());
		return idMap;
	}

}
