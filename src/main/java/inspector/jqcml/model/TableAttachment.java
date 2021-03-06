package inspector.jqcml.model;

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

import com.google.common.base.MoreObjects;
import inspector.jqcml.jaxb.adapters.TableAttachmentAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * Contains tabular data for an {@link AttachmentParameter}.
 */
// JAXB
//member variables don't need further JAXB annotations (these are included in the TableAttachmentString class)
@XmlJavaTypeAdapter(TableAttachmentAdapter.class)
// JPA
@Entity
@Table(name="table_attachment")
public class TableAttachment {

    // JAXB
    @XmlTransient
    // JPA
    @Transient
    private static final Logger LOGGER = LogManager.getLogger(TableAttachment.class);

    /** read-only qcDB primary key; generated by JPA */
    @Id
    @TableGenerator(name="pk_table", table="pk_sequence", pkColumnName="name",
            valueColumnName="seq", pkColumnValue="table_attachment", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="pk_table")
    @Column(name="TA_ID_PK")
    private int primaryKey;

    /** The columns of the table. Each column is defined by either a name, or (textual) reference to a term in a cv. */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentTable")
    @MapKey(name="column")
    private Map<String, TableColumn> columns;
    /** the rows of the table containing the individual values */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="parentTable")
    @MapKey(name="row")
    private Map<Integer, TableRow> rows;

    /** inverse part of the bi-directional relationship with {@link AttachmentParameter} */
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="AP_ID_FK", referencedColumnName="AP_ID_PK")
    private AttachmentParameter parentAttachment;

    /**
     * Constructs a new empty TableAttachment object.
     */
    public TableAttachment() {
        columns = new HashMap<>();
        rows = new HashMap<>();
    }

    /**
     * Constructs a new TableAttachment object with the given named columns.
     *
     * @param columns  the names of the table's columns
     */
    public TableAttachment(String[] columns) {
        this();

        for(String column : columns) {
            addColumn(column);
        }
    }

    /**
     * Adds a new column with the specified header information to the table.
     *
     * If a column with the same header information already exists, this column is retained.
     *
     * @param columnName  the header information of the new column
     */
    public void addColumn(String columnName) {
        // check if the column is valid
        if(columnName == null) {
            LOGGER.error("Can't add <null> Column to a TableAttachment object");
            throw new NullPointerException("Can't add <null> Column");
        } else if(columns.get(columnName) == null) {
            // check if this column already exists
            TableColumn column = new TableColumn(columnName);
            column.setParentTable(this);
            columns.put(columnName, column);
        }
        // else: retain the old column
    }

    /**
     * Returns a collection of all {@link TableColumn}s in this table.
     *
     * @return  a collection of all columns
     */
    public Collection<TableColumn> getColumns() {
        return columns.values();
    }

    /**
     * Returns a collection of all {@link TableRow}s in this table.
     *
     * @return  a collection of all rows
     */
    public Collection<TableRow> getRows() {
        return rows.values();
    }

    /**
     * Adds a new value to this table specified by the given column and row.
     *
     * If the specified column and row already contain a value, the old value is overwritten by the given value.
     *
     * If the table doesn't contain the given column, an exception is thrown.
     * New rows are added on the fly to the table when adding values.
     *
     * @param column  the column of the value
     * @param row  the row of the value
     * @param value  the new value to be added to the table
     */
    public void addValue(String column, int row, String value) {
        // check if the column exists
        // if not, throw an exception
        TableColumn tc = columns.get(column);
        if(tc == null) {
            LOGGER.error("Column <{}> doesn't exist", column);
            throw new IllegalArgumentException("Column <" + column + "> doesn't exist");
        }
        // check if the row exists
        // if not, add it
        TableRow tr = rows.get(row);
        if(tr == null) {
            // check if the row number is valid
            if(row < 0) {
                LOGGER.error("Invalid row number <{}>", row);
                throw new IllegalArgumentException("Invalid row number <" + row + ">");
            }
            tr = new TableRow(row);
            tr.setParentTable(this);
            rows.put(row, tr);
        }

        // check if the table already contains a value for the given column and row
        // and remove the old value if necessary
        removeValue(column, row);

        // add the new value to the column and row
        new TableValue(tc, tr, value);
    }

    /**
     * Removes the value in the given column and row from the table.
     *
     * @param column  the column of the value to be removed
     * @param row  the row of the value to be removed
     */
    public void removeValue(String column, int row) {
        // check if a value exists for the given column and row
        TableValue value = getValue(column, row);
        if(value != null) {
            // remove the value from the column and row
            value.getColumn().removeValue(value);
            value.getRow().removeValue(value);
            // remove the reference to the column and row
            // (this has to happen after both removals have been done because the removal depends on the equals-method)
            value.removeFromTable();
        }
    }

    /**
     * Retrieves the {@link TableValue} specified by the given column and row from this table.
     *
     * @param column  the column of the value to be retrieved
     * @param row  the row of the value to be retrieved
     * @return  the TableValue specified by the given column and row if present, {@code null} otherwise
     */
    public TableValue getValue(String column, int row) {
        TableColumn tc = columns.get(column);
        TableRow tr = rows.get(row);
        // check if the row and column exist
        if(tc == null || tr == null) {
            return null;
        }

        boolean columnSetIsLarger = tc.getValues().size() > tr.getValues().size();
        Set<TableValue> tempSet = new HashSet<>(columnSetIsLarger ? tr.getValues() : tc.getValues());
        // retain all values present in both sets
        // the result should be a single value specified by the given column and row
        tempSet.retainAll(columnSetIsLarger ? tc.getValues() : tr.getValues());
        // check if a value was found
        return !tempSet.isEmpty() ? tempSet.iterator().next() : null;
    }

    /**
     * Returns this table as a two-dimensional string array.
     *
     * @return a two-dimensional array containing the data in this table
     */
    public String[][] toArray() {
        // array dimension
        int maxRow = -1;
        for(TableRow tr : getRows()) {
            if(tr.getRow() > maxRow) {
                maxRow = tr.getRow();
            }
        }
        String[][] array = new String[maxRow + 2][columns.size()];

        // the first row contains the column information
        int col = 0;
        for(TableColumn tc : getColumns()) {
            array[0][col++] = tc.getColumn();
        }
        // sort the columns alphabetically to get a ordering
        Arrays.sort(array[0]);

        // subsequent rows contain the values
        for(int row = 0; row <= maxRow; row++) {
            for(col = 0; col < array[0].length; col++) {
                TableValue value = getValue(array[0][col], row);
                if(value != null) {
                    array[row + 1][col] = value.getValue();
                }
            }
        }

        return array;
    }

    /**
     * Returns the parent {@link AttachmentParameter} object to which this TableAttachment belongs.
     *
     * @param parent  the parent AttachmentParameter object
     */
    public void setParentAttachment(AttachmentParameter parent) {
        this.parentAttachment = parent;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("table", Arrays.toString(toArray())).toString();
    }
}
