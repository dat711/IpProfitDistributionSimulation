package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidTotalBenefit;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class TotalBenefitValidator implements ConstraintValidator<ValidTotalBenefit, Set<ParticipantDto>>{
    @Override
    public boolean isValid(Set<ParticipantDto> field, ConstraintValidatorContext context ){
        double total = field.stream().mapToDouble(ParticipantDto::getPercentage).sum();
        if (total == 1.0){
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.format("The total benefit of the is not add up to 1.0 but being %s instead", total))
                .addPropertyNode("Invalid-total-benefit")
                .addConstraintViolation();
        return false;
    }
}
