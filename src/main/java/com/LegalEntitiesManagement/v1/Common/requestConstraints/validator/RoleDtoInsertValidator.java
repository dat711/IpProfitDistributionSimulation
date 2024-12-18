package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.InsertRoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleDtoInsertValidator implements ConstraintValidator<InsertRoleDto, RoleDto> {
    @Override
    public boolean isValid(RoleDto request, ConstraintValidatorContext context ){

        DtoInsertChecking.NullInsertErrorDetails nullInsertErrorDetails = DtoInsertChecking
                .isInsertRequestValid(request.getClass(), request);

        boolean isValid = nullInsertErrorDetails.isValid();

        if (!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(nullInsertErrorDetails.message())
                    .addPropertyNode("Null-fields:").addConstraintViolation();
        }

        return isValid;
    }
}
