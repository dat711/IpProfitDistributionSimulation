package com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.DtoInsertChecking;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.IpBasedContractInsertValidator;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractCompositionDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InsertContractValidator implements ConstraintValidator<IpBasedContractInsertValidator, IpBasedContractCompositionDto> {
    @Override
    public boolean isValid(IpBasedContractCompositionDto request, ConstraintValidatorContext context ){
        DtoInsertChecking.NullInsertErrorDetails nullInsertErrorDetails = DtoInsertChecking
                .isInsertRequestValid(request.getContractDto().getClass(), request.getContractDto());

        boolean isValid = nullInsertErrorDetails.isValid();

        if (!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(nullInsertErrorDetails.message())
                    .addPropertyNode("Null-fields:").addConstraintViolation();
        }

        return isValid;
    }
}
