package inspector.jqcml.io;

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
import inspector.jqcml.model.QcML;
import inspector.jqcml.model.QualityAssessment;

import java.util.Iterator;

/**
 * A qcML input reader.
 */
public interface QcMLReader {

    /** The version of the current qcML XML schema **/
    public static final String QCML_VERSION = "0.0.8";

    /**
     * Returns the full {@link QcML} object specified by the given file.
     *
     * Warning: Because a single QcML object can optionally contain several {@link QualityAssessment}s,
     * only use this method when you are certain the requested QcML object won't be too big,
     * in order to prevent running out of main memory.
     *
     * @param qcmlFile  The file name of the qcML file from which the Reader will read
     * @return The {@link QcML} object specified by this reader if found, else {@code null}
     */
    public QcML getQcML(String qcmlFile);

    /**
     * Returns the {@link Cv} object with the given id from the given file.
     *
     * @param qcmlFile  The file name of the qcML file from which the Reader will read
     * @param id  The identifier of the requested Cv object
     * @return The {@link Cv} object specified by the given id if present, else {@code null}
     */
    public Cv getCv(String qcmlFile, String id);

    /**
     * Returns an {@link Iterator} over all {@link Cv} objects in the given file.
     *
     * @param qcmlFile  The file name of the qcML file from which the Reader will read
     * @return An {@link Iterator} over all {@link Cv} objects
     */
    public Iterator<Cv> getCvIterator(String qcmlFile);

    /**
     * Returns the {@link QualityAssessment} object with the given id from the given file.
     *
     * @param qcmlFile  The file name of the qcML file from which the Reader will read
     * @param id  The identifier of the requested QualityAssessment object
     * @return The {@link QualityAssessment} object specified by the given id if present, else {@code null}
     */
    public QualityAssessment getQualityAssessment(String qcmlFile, String id);

    /**
     * Returns an {@link Iterator} over all {@link QualityAssessment} objects in the given file.
     *
     * @param qcmlFile  The file name of the qcML file from which the Reader will read
     * @return An {@link Iterator} over all {@link QualityAssessment} objects
     */
    public Iterator<QualityAssessment> getQualityAssessmentIterator(String qcmlFile);
}
