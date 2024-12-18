package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.InsertContractValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(validatedBy = InsertContractValidator.class)
public @interface IpBasedContractInsertValidator {
    String message() default "There exist null field in the post request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
