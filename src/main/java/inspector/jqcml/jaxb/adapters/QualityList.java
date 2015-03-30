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
