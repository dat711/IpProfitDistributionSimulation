package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.RoleDtoInsertValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(validatedBy = RoleDtoInsertValidator.class)
public @interface InsertRoleDto {
    String message() default "There exist null field in the post request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
