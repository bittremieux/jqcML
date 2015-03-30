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
