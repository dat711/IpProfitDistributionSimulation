package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ExistsConstraint;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistsValidator implements ConstraintValidator<ExistsConstraint, Object> {
    private final EntitiesCrudService entitiesCrudService;
    public ExistsValidator(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    private String entity;

    @Override
    public void initialize(ExistsConstraint constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation if needed
        }

        boolean exists = switch (entity.toLowerCase()) {
            case "role" -> entitiesCrudService.roleExists((Long) value);
            case "stakeholder" -> entitiesCrudService.stakeholderExists((Long) value);
            case "intellectualproperty" -> entitiesCrudService.intellectualPropertyExists((Long) value);
            default -> throw new IllegalArgumentException("Unknown entity type: " + entity);
        };

        if (!exists) {
            String errorMessage = String.format("%s with id '%s' does not exist", entity, value);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }

        return exists;
    }
}
