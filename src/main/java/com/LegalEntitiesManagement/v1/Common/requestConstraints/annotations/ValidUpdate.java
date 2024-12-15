package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.UpdateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateValidator.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)

public @interface ValidUpdate {
    String message() default "Invalid update request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String entity();
}
