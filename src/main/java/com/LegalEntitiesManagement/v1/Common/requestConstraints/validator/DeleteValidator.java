package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidDelete;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DeleteValidator implements ConstraintValidator<ValidDelete, Long>{
    private final EntitiesCrudService entitiesCrudService;
    private String clazz;
    @Override
    public void initialize(ValidDelete validDelete) {
        this.clazz = validDelete.entity();
    }

    public DeleteValidator(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context){
        if (clazz.equals("stakeholder")){
            return canDeleteStakeHolder(id, context);
        }

        if (clazz.equals("role")){
            return canDeleteRole(id, context);
        }

        return true;

    }

    private boolean canDeleteStakeHolder(Long id, ConstraintValidatorContext context){
        boolean isValid = true;
        if (!entitiesCrudService.stakeholderExists(id)){
            return false;
        }

        if (!entitiesCrudService.findParticipantsByStakeHolderId(id).isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("StakeHolder is still engaged in contracts")
                    .addPropertyNode("StakeHolder-as-aependencies")
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean canDeleteRole(Long id, ConstraintValidatorContext context){
        boolean isValid = true;

        if(!entitiesCrudService.roleExists(id)){
            return false;
        }

        if(entitiesCrudService.stakeHolderExistByRoleId(id)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Exist StakeHolder with this Role")
                    .addPropertyNode("Role-as-dependencies")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
