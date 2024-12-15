package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.DeleteValidator;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DeleteValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
public @interface ValidDelete {
    String message() default "Exist dependencies of this Stakeholder";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String entity();
}
