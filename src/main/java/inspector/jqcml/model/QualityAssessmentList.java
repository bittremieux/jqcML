package inspector.jqcml.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Intermediate class to assist in the JAXB-conversion from a {@link QualityAssessment} object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="qualityAssessmentType")
public class QualityAssessmentList implements Iterable<CvParameter> {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name="ID")
    @XmlID
    @XmlAttribute(name="ID", required=true)
    private String id;
    @XmlElementRef
    private List<CvParameter> paramList;
    @XmlTransient
    private boolean isSet;

    public QualityAssessmentList() {
        paramList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addParameter(CvParameter param) {
        paramList.add(param);
    }

    @Override
    public Iterator<CvParameter> iterator() {
        return paramList.iterator();
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }

}
