package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidTotalBenefit;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class TotalBenefitValidator implements ConstraintValidator<ValidTotalBenefit, Set<ParticipantDto>>{
    @Override
    public boolean isValid(Set<ParticipantDto> field, ConstraintValidatorContext context ){
        System.out.println("Do validate total benefit ");
        double total = field.stream().mapToDouble(ParticipantDto::getPercentage).sum();

        if (field.isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The participants must not be empty")
                    .addPropertyNode("Invalid-participants")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;

        long numExecutor = field.stream().filter(ParticipantDto::getIsExecutor).count();
        if(numExecutor != 1){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The number of executor must equal 1")
                    .addPropertyNode("Invalid-number-executors")
                    .addConstraintViolation();
            isValid = false;
        }

        if (total != 1.0){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("The total benefit of the is not add up to 1.0 but being %s instead", total))
                    .addPropertyNode("Invalid-total-benefit")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
