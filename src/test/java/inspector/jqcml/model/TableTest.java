package inspector.jqcml.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableTest {
	
	@Test
	public void tableValueType() {
		TableValue tvInt = new TableValue(null, null, "1");
		assertEquals(TableValueType.INTEGER, tvInt.getType());

		TableValue tvDbl = new TableValue(null, null, "3.14");
		assertEquals(TableValueType.DOUBLE, tvDbl.getType());

		TableValue tvStr = new TableValue(null, null, "some value");
		assertEquals(TableValueType.STRING, tvStr.getType());
	}

	@Test(expected = NullPointerException.class)
	public void rowAddValue_null() {
		TableRow row = new TableRow();
		row.addValue(null);
	}

	@Test(expected = NullPointerException.class)
	public void columnAddValue_null() {
		TableColumn column = new TableColumn();
		column.addValue(null);
	}
}
