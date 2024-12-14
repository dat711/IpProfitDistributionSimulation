package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.StakeHolderDeleteValidator;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StakeHolderDeleteValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
public @interface ValidStakeHolderDelete {
    String message() default "Exist dependencies of this Stakeholder";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
