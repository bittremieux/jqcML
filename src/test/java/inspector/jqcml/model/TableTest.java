package inspector.jqcml.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}
