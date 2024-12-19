package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.IpBasedContractBatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpBasedContractBatchValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIpBasedContractBatch {
    String message() default "Invalid IP-based contract batch";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
    @Constraint(validatedBy = IpBasedContractBatchValidator.InsertContractValidator.class)
    @interface IpBasedContractInsertValidator {
        String message() default "There exist null field in the post request";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
