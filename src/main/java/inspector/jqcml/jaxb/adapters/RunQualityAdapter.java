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

import javax.xml.bind.annotation.XmlElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converts between a simple list of {@link QualityAssessment}s representing a {@code runQuality} and a {@link Map} of {@link QualityAssessment}s representing a {@code runQuality} indexed by their id.
 */
public class RunQualityAdapter extends QualityListAdapter<RunQualityList> {

    public RunQualityAdapter() {
        isSet = false;
        myType = RunQualityList.class;
    }
}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link QualityAssessment} objects representing a {@code runQuality}.
 */
class RunQualityList extends QualityList {

    public RunQualityList() {
        super();
    }

    public RunQualityList(Collection<QualityAssessment> qaCollection) {
        super(qaCollection);
    }

    @XmlElement(name="runQuality")
    @Override
    public List<QualityAssessment> getQualityAssessmentList() {
        return super.getQualityAssessmentList();
    }

    @Override
    public void setQualityAssessmentList(List<QualityAssessment> qaList) {
        super.setQualityAssessmentList(qaList);
    }


}
