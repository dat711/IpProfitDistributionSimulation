package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.StakeHolderUpdateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StakeHolderUpdateValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)

public @interface ValidStakeHolderUpdate {
    String message() default "Invalid update request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
