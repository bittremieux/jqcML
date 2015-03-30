package inspector.jqcml.jpa.customizer;

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

import inspector.jqcml.model.AbstractParameter;
import inspector.jqcml.model.Threshold;
import inspector.jqcml.model.QualityAssessment;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Customizer to remove the relationship between {@link Threshold} and {@link QualityAssessment} present in {@link AbstractParameter}.
 * 
 * Threshold inherits from AbstractParameter, but doesn't use a foreign key to QualityAssessment, as opposed to regular parameters.
 */
public class ThresholdCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) {
        descriptor.removeMappingForAttributeName("parentAssessment");
    }

}
