package inspector.jqcml.jaxb.adapters;

import inspector.jqcml.model.QualityAssessment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link QualityAssessment} objects.
 */
// the accessor type has to be property because of inheritance by RunQualityList and SetQualityList
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class QualityList implements Iterable<QualityAssessment> {

    protected List<QualityAssessment> qaList;

    public QualityList() {
        qaList = new ArrayList<>();
    }

    public QualityList(Collection<QualityAssessment> qaCollection) {
        this.qaList = new ArrayList<>(qaCollection);
    }

    @XmlTransient
    public List<QualityAssessment> getQualityAssessmentList() {
        return qaList;
    }

    public void setQualityAssessmentList(List<QualityAssessment> qaList) {
        this.qaList = qaList;
    }

    public void addElement(QualityAssessment elem) {
        qaList.add(elem);
    }

    public int size() {
        return qaList.size();
    }

    public Iterator<QualityAssessment> iterator() {
        return qaList.iterator();
    }

}
