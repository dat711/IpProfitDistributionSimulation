package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.RoleDtoInsertValidator;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.TotalBenefitValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = TotalBenefitValidator.class)
public @interface ValidTotalBenefit {
    String message() default "The total benefit of a contract must add up to one";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
