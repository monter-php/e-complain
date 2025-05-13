package tgi.ecomplain.api.complain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ComplainStatusValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidComplainStatus {
    String message() default "Invalid complain status. Must be one of: SUBMITTED, IN_PROGRESS, APPROVED, REJECTED, CANCELLED, RESOLVED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
