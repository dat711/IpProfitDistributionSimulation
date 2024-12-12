package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ValidTotalBenefit {

}
