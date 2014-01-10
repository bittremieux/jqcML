package inspector.jqcml.jpa.customizer;

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
