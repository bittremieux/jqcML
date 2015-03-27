package inspector.jqcml.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableTest {
	
	@Test
	public void tableValueType() {
		TableColumn column = new TableColumn("column");
		TableRow row = new TableRow(0);

		TableValue tvInt = new TableValue(column, row, "1");
		assertEquals(TableValueType.INTEGER, tvInt.getType());

		TableValue tvDbl = new TableValue(column, row, "3.14");
		assertEquals(TableValueType.DOUBLE, tvDbl.getType());

		TableValue tvStr = new TableValue(column, row, "some value");
		assertEquals(TableValueType.STRING, tvStr.getType());
	}

	@Test(expected = NullPointerException.class)
	public void rowAddValue_null() {
		TableRow row = new TableRow(0);
		row.addValue(null);
	}

	@Test(expected = NullPointerException.class)
	public void columnAddValue_null() {
		TableColumn column = new TableColumn("column");
		column.addValue(null);
	}
}
