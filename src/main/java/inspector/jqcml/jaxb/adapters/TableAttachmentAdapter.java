package inspector.jqcml.jaxb.adapters;

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

import inspector.jqcml.model.TableAttachment;
import inspector.jqcml.model.TableAttachmentString;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts between a {@link TableAttachmentString} and a {@link TableAttachment}. 
 */
public class TableAttachmentAdapter extends XmlAdapter<TableAttachmentString, TableAttachment> {

    @Override
    public TableAttachmentString marshal(TableAttachment ta) throws Exception {
        // can be null if the attachment contains a binary instead of a table
        if(ta == null) {
            return null;
        } else {
            // convert the table to a two-dimensional array
            String[][] array = ta.toArray();

            // convert to a TableStringAttachment
            TableAttachmentString tas = new TableAttachmentString();

            // convert the columns to a single String
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < array[0].length; i++) {
                sb.append(array[0][i]);
                if(i < array[0].length - 1) {
                    sb.append(' ');
                }
            }
            tas.setTableHeader(sb.toString());

            // convert the values to a String array
            // each row is a single string
            String[] body = new String[array.length-1];
            for(int row = 1; row < array.length; row++) {
                sb = new StringBuilder();
                for(int col = 0; col < array[0].length; col++) {
                    sb.append(array[row][col]);
                    if(col < array[0].length - 1) {
                        sb.append(' ');
                    }
                }
                body[row-1] = sb.toString();
            }
            tas.setTableBody(body);

            return tas;
        }
    }

    @Override
    public TableAttachment unmarshal(TableAttachmentString tas) throws Exception {
        TableAttachment ta = new TableAttachment();

        String[] header = tas.getTableHeader().split("\\s+");
        String[] body = tas.getTableBody();

        // create the columns
        for(String column : header) {
            ta.addColumn(column);
        }

        // add the values
        for(int row = 0; row < body.length; row++) {
            String[] rowArr = body[row].split("\\s+");
            for(int column = 0; column < rowArr.length; column++) {
                ta.addValue(header[column], row, rowArr[column]);
            }
        }

        return ta;
    }

}
