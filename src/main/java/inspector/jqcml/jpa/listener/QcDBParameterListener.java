package inspector.jqcml.jpa.listener;

import inspector.jqcml.model.Cv;
import inspector.jqcml.model.CvParameter;

import javax.persistence.PostLoad;

/**
 * Sets references to a {@link Cv} in a parameter in compliance to be used with JAXB after retrieval from a qcDB.
 */
public class QcDBParameterListener {

    /**
     * JPA retrieves cvRefs (and unitCvRefs) through the primary key of the {@link Cv}'s, and sets a pointer to the Cv.
     * JAXB needs a String representation of the ID of the Cv instead. Therefore the ID's are explicitly added to each parameter.
     *
     * @param param  the parameter for which the ID of the Cv's will be set.
     */
    @PostLoad
    private void resolveCvRefs(CvParameter param) {
        // these methods can throw a NullPointerException or an IllegalArgumentException if an invalid Cv is given
        // don't catch the exceptions
        param.setCvRef(param.getCvRef());
        param.setUnitCvRef(param.getUnitCvRef());
    }

}
