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

import inspector.jqcml.model.TableValueType;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;

/**
 * Converts the enum field name from {@link TableValueType} to a custom String.
 */
public class TableValueTypeCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        DirectToFieldMapping typeMapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName("type");
        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("integer", TableValueType.INTEGER);
        converter.addConversionValue("double", TableValueType.DOUBLE);
        converter.addConversionValue("string", TableValueType.STRING);
        typeMapping.setConverter(converter);
    }

}
