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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Intermediate class to assist in the JAXB-conversion from a {@link TableAttachment} object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TableAttachmentString {

    @XmlElement(name="tableColumnTypes")
    private String tableHeader;
    @XmlElement(name="tableRowValues")
    private String[] tableBody;

    public TableAttachmentString() {
        // do nothing
    }

    public String getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(String header) {
        this.tableHeader = header;
    }

    public String[] getTableBody() {
        return tableBody.clone();
    }

    public void setTableBody(String[] body) {
        this.tableBody = body.clone();
    }
}
