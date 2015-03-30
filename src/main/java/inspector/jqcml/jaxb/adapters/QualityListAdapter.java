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

import inspector.jqcml.model.QualityAssessment;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts between a simple list of {@link QualityAssessment}s (encapsulated in a {@link QualityList}) and a {@link Map} of {@link QualityAssessment}s indexed by their id.
 * 
 * @param <T>  Indicates whether we are converting a {@code runQuality} or a {@code setQuality}
 */
public class QualityListAdapter<T extends QualityList> extends XmlAdapter<T, Map<String, QualityAssessment>> {

    protected boolean isSet;
    protected Class<T> myType;

    @Override
    public T marshal(Map<String, QualityAssessment> qaMap) throws Exception {
        return myType.getConstructor(Collection.class).newInstance(qaMap.values());
    }

    @Override
    public Map<String, QualityAssessment> unmarshal(T qaList) throws Exception {
        Map<String, QualityAssessment> qaMap = new TreeMap<>();
        for(QualityAssessment qa : qaList) {
            // set run/setQuality flag
            qa.setSet(isSet);
            // add it to the map
            qaMap.put(qa.getId(), qa);
        }

        return qaMap;
    }

}
