package tgi.ecomplain.api.complain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tgi.ecomplain.domain.complain.ComplainStatus;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ComplainStatusValidator implements ConstraintValidator<ValidComplainStatus, String> {

    private Set<String> allowedStatuses;

    @Override
    public void initialize(ValidComplainStatus constraintAnnotation) {
        allowedStatuses = Arrays.stream(ComplainStatus.values())
                                .map(ComplainStatus::name)
                                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Or false, depending on whether status can be null/empty. Assuming true for now if @NotBlank is handled separately.
        }
        return allowedStatuses.contains(value.toUpperCase());
    }
}
