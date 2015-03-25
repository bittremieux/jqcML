package inspector.jqcml.io.db;

/**
 * Provides a mapping between a primary key in a database and an identifier for a certain table.
 */
public class IdKeyPair {

    /** the (textual) unique identifier */
    private String id;
    /** the (integer) primary key in the database */
    private int key;

    /**
     * Constructs a mapping between the given key and identifier.
     *
     * @param id  the database key
     * @param key  the unique identifier
     */
    public IdKeyPair(String id, int key) {
        setId(id);
        setKey(key);
    }

    /**
     * Returns the identifier that constitutes the mapping.
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier that constitutes the mapping.
     *
     * @param id  the identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the key that constitutes the mapping.
     *
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the key that constitutes the mapping.
     *
     * @param key  the key
     */
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "(id="+ id + ", key=" + key + ")";
    }

}
