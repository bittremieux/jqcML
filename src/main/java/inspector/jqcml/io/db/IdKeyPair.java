package inspector.jqcml.io.db;

/*
 * #%L
 * jqcML
 * %%
 * Copyright (C) 2013 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
