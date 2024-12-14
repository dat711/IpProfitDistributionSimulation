package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidStakeHolderDelete;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StakeHolderDeleteValidator implements ConstraintValidator<ValidStakeHolderDelete, Long>{
    private final EntitiesCrudService entitiesCrudService;

    public StakeHolderDeleteValidator(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context){
        boolean isValid = true;
        if (!entitiesCrudService.stakeholderExists(id)){
            return false;
        }

        if (!entitiesCrudService.findParticipantsByStakeHolderId(id).isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("StakeHolder is still engaged in contracts")
                    .addPropertyNode("StakeHolder-As-Dependencies")
                    .addConstraintViolation();
        }
        return isValid;
    }

}
