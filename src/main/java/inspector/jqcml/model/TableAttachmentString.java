package inspector.jqcml.model;

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
		return tableBody;
	}

	public void setTableBody(String[] body) {
		this.tableBody = body;
	}
}
