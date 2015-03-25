package inspector.jqcml.model;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private static final String NAMESPACE = "http://www.prime-xs.eu/ms/qcml";

    @XmlElementDecl(name="runQuality")
    public JAXBElement<QualityAssessmentList> createRunQuality(QualityAssessmentList qaList) {
        return new JAXBElement<>(new QName(NAMESPACE, "runQuality"), QualityAssessmentList.class, qaList);
    }

    @XmlElementDecl(name="setQuality")
    public JAXBElement<QualityAssessmentList> createSetQuality(QualityAssessmentList qaList) {
        return new JAXBElement<>(new QName(NAMESPACE, "setQuality"), QualityAssessmentList.class, qaList);
    }
}
