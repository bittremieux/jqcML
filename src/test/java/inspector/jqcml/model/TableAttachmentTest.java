package inspector.jqcml.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TableAttachmentTest {

    private TableAttachment table;

    @Before
    public void setUp() {
        // create a dummy table
        table = new TableAttachment();
        // add columns
        for(int col = 0; col < 5; col++)
            table.addColumn("column " + col);
        // add values
        for(int row = 0; row < 10; row++)
            for(int col = 0; col < 5; col++)
                table.addValue("column " + col, row, "(" + row + ", " + col + ")");
    }

    @Test(expected=NullPointerException.class)
    public void addColumn_null() {
        table.addColumn(null);
    }

    @Test
    public void addColumn_new() {
        assertEquals(5, table.getColumns().size());

        String columnName = "new column";
        table.addColumn(columnName);

        assertEquals(6, table.getColumns().size());

        String columnNameOther = "some other column";
        table.addColumn(columnNameOther);

        assertEquals(7, table.getColumns().size());
    }

    @Test
    public void addColumn_duplicate() {
        assertEquals(5, table.getColumns().size());

        String columnName = "column 0";
        table.addColumn(columnName);

        assertEquals(5, table.getColumns().size());

        String columnNameOther = "column 4";
        table.addColumn(columnNameOther);

        assertEquals(5, table.getColumns().size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void addValue_nullColumn() {
        table.addValue(null, 0, "value");
    }

    @Test(expected=IllegalArgumentException.class)
    public void addValue_nonExistingColumn() {
        table.addValue("non-existing column", 0, "value");
    }

    @Test(expected=IllegalArgumentException.class)
    public void addValue_invalidRow() {
        assertEquals(5, table.getColumns().size());
        assertEquals(10, table.getRows().size());

        table.addValue("column 2", -12, "value");
    }

    @Test
    public void addValue_replace() {
        assertEquals(5, table.getColumns().size());
        assertEquals(10, table.getRows().size());
        assertEquals("(3, 2)", table.getValue("column 2", 3).getValue());

        String newValue = "new value";
        table.addValue("column 2", 3, newValue);

        assertEquals(5, table.getColumns().size());
        assertEquals(10, table.getRows().size());
        assertEquals(newValue, table.getValue("column 2", 3).getValue());
    }

    @Test
    public void addValue_newRow() {
        assertEquals(5, table.getColumns().size());
        assertEquals(10, table.getRows().size());

        String newValue = "new value in a new row";
        table.addValue("column 3", 25, newValue);

        assertEquals(5, table.getColumns().size());
        assertEquals(11, table.getRows().size());
        assertEquals(newValue, table.getValue("column 3", 25).getValue());
    }

    @Test
    public void addValue_newColumn() {
        assertEquals(5, table.getColumns().size());
        assertEquals(10, table.getRows().size());

        String newColumn = "new column";
        String newValue = "new value in a new column";
        table.addColumn(newColumn);
        table.addValue(newColumn, 17, newValue);

        assertEquals(6, table.getColumns().size());
        assertEquals(11, table.getRows().size());
        assertEquals(newValue, table.getValue(newColumn, 17).getValue());
    }

    @Test
    public void removeValue_nullColumn() {
        table.removeValue(null, 0);
    }

    @Test
    public void removeValue_nonExistingColumn() {
        table.removeValue("non-existing column", 1);
    }

    @Test
    public void removeValue_invalidRow() {
        table.removeValue("column 0", -21);
    }

    @Test
    public void removeValue_nonExistingRow() {
        table.removeValue("column 0", 83);
    }

    @Test
    public void removeValue_valid() {
        assertNotNull(table.getValue("column 1", 7));

        TableValue value = table.getValue("column 1", 7);
        table.removeValue("column 1", 7);

        assertNull(table.getValue("column 1", 7));
        assertNull(value.getColumn());
        assertNull(value.getRow());
    }

    @Test
    public void getValue_nullColumn() {
        table.getValue(null, 0);
    }

    @Test
    public void getValue_nonExistingColumn() {
        table.getValue("non-existing column", 1);
    }

    @Test
    public void getValue_invalidRow() {
        table.getValue("column 0", -21);
    }

    @Test
    public void getValue_nonExistingRow() {
        table.getValue("column 0", 83);
    }

    @Test
    public void getValue_valid() {
        assertEquals("(7, 1)", table.getValue("column 1", 7).getValue());
    }

}
