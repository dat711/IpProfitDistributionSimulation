package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidUpdate;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

/* To do:
*  Refactor the code to method constraints validator aka cross parameter validator so that I can finish it. haha. so happy
* */

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class UpdateValidator implements ConstraintValidator<ValidUpdate, Object[]>{
    private final EntitiesCrudService entitiesCrudService;

    private String entity;

    @Override
    public void initialize(ValidUpdate validUpdate){
        this.entity = validUpdate.entity();
    }

    public UpdateValidator(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @Override
    public boolean isValid(Object[] parameter, ConstraintValidatorContext context) {
        Long id = (Long) parameter[0];
        if(entity.equals("stakeholder")){
            return canUpdateStakeHolder(id, (StakeHolderDto) parameter[1], context);
        }

        if (entity.equals("role")){
            return canUpdateRole(id, (RoleDto) parameter[1], context);
        }

        return true;
    }

    private boolean canUpdateRole(Long id, RoleDto updateDto, ConstraintValidatorContext context){
        if (updateDto == null) {
            return false;
        }

        if (!entitiesCrudService.roleExists(id)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Role do not exist")
                    .addPropertyNode("Role")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;
        RoleDto currentRole = entitiesCrudService.getRole(id);

        if (entitiesCrudService.stakeHolderExistByRoleId(id)
            && currentRole.getPriority() != updateDto.getPriority()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Exist StakeHolder with this Role, so we can not update its priority")
                    .addPropertyNode("Role-as-dependencies")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }


    private boolean canUpdateStakeHolder(Long id, StakeHolderDto updateDto, ConstraintValidatorContext context){
        if (updateDto == null) {
            return false;
        }

        if (!entitiesCrudService.stakeholderExists(id)){
            context.buildConstraintViolationWithTemplate("StakeHolder do not exist")
                    .addPropertyNode("StakeHolder")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;

        // Get the original StakeHolder
        StakeHolderDto originalDto = entitiesCrudService.getStakeHolder(id);

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
