package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidStakeHolderUpdate;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StakeHolderUpdateValidator implements ConstraintValidator<ValidStakeHolderUpdate, StakeHolderDto>{
    private final EntitiesCrudService entitiesCrudService;

    public StakeHolderUpdateValidator(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @Override
    public boolean isValid(StakeHolderDto updateDto, ConstraintValidatorContext context) {
        if (updateDto == null) {
            return false;
        }
        Long pathId = ValidatorHelpers.getPathID();

        boolean isValid = true;

        if(updateDto.getId() != null && !updateDto.getId().equals(pathId)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("StakeHolder ID must be equal the path id")
                    .addPropertyNode("StakeHolder-Id")
                    .addConstraintViolation();
            return false;
        }

        if(updateDto.getName() == null || updateDto.getName().trim().isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("StakeHolder name must be not empty or null")
                    .addPropertyNode("StakeHolder-Name")
                    .addConstraintViolation();
            isValid = false;
        }

        // Get the original StakeHolder
        StakeHolderDto originalDto = entitiesCrudService.getStakeHolder(pathId);

        // Validate roleId hasn't changed
        if (!originalDto.getRoleId().equals(updateDto.getRoleId())) {
            context.buildConstraintViolationWithTemplate("Role ID cannot be modified, if you need the identical stakeholder with the new Role, create a new one, or delete this one and recreate")
                    .addPropertyNode("roleId")
                    .addConstraintViolation();
            isValid = false ;
        }

        // Only validate name changes
        return isValid;
    }
}
