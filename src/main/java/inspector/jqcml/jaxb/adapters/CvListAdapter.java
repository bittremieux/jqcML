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

import inspector.jqcml.model.Cv;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.*;

/**
 * Converts between a simple list of {@link Cv}s (encapsulated in a {@link CvList}) and a {@link Map} of {@link Cv}s indexed by their id.
 */
public class CvListAdapter extends XmlAdapter<CvList, Map<String, Cv>> {

    @Override
    public CvList marshal(Map<String, Cv> cvMap) throws Exception {
        return new CvList(cvMap.values());
    }

    @Override
    public Map<String, Cv> unmarshal(CvList cvList) throws Exception {
        Map<String, Cv> cvMap = new TreeMap<>();
        for(Cv cv : cvList) {
            cvMap.put(cv.getId(), cv);
        }

        return cvMap;
    }

}

/**
 * Intermediate class to assist in the JAXB-conversion from a list of {@link Cv} objects.
 */
@XmlType(name="cvListType")
class CvList implements Iterable<Cv> {

    @XmlElement(name="cv", required=true)
    private List<Cv> cvs;

    public CvList() {
        cvs = new ArrayList<>();
    }

    public CvList(Collection<Cv> cvCollection) {
        this.cvs = new ArrayList<>(cvCollection);
    }

    public void addElement(Cv elem) {
        cvs.add(elem);
    }

    public int size() {
        return cvs.size();
    }

    public Iterator<Cv> iterator() {
        return cvs.iterator();
    }


}
