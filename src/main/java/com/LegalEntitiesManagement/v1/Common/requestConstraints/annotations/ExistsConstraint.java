package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.ExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Added PARAMETER to support method parameters
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsConstraint {
    String message() default "Entity does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String entity(); // e.g., "Role", "StakeHolder"
}