package inspector.jqcml.jpa.customizer;

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
