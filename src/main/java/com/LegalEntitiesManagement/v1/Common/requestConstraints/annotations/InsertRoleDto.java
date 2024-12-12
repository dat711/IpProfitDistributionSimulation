package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.RoleDtoInsertValidator;
import jakarta.validation.Constraint;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(validatedBy = RoleDtoInsertValidator.class)
public @interface InsertRoleDto {}
