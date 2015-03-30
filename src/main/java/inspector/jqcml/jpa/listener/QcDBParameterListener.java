package inspector.jqcml.jpa.listener;

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
