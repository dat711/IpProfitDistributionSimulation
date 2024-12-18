package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidIpBasedContractBatch;

import java.util.List;

public class IpBasedContractBatchValidator implements ConstraintValidator<ValidIpBasedContractBatch, List<IpBasedContractCompositionDto>> {

    @Override
    public boolean isValid(List<IpBasedContractCompositionDto> contracts, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (contracts == null || contracts.isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The request is empty")
                    .addPropertyNode("Empty Request: ").addConstraintViolation();
            return false;
        }

        if (contracts.stream().noneMatch(this::validateSingleContract)){
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The request is invalid")
                    .addPropertyNode("At least one of the contract of the batch violate rules.").addConstraintViolation();
        }

        return isValid;
    }

    private boolean validateSingleContract(IpBasedContractCompositionDto dto){
        if (dto.getContractDetail().getIpId() == null || dto.getContractDetail().getIpId() < 1){
            return false;
        }

        if (dto.getContractDetail().getContractPriority() == null || dto.getContractDetail().getContractPriority() < 0){
            return false;
        }

        if (dto.getContractDetail().getContractActiveDate() == null ){
            return false;
        }

        if (dto.getContractDetail().getDescription() == null || dto.getContractDetail().getDescription().trim().isEmpty()){
            return false;
        }

        if (dto.getParticipants() == null || dto.getParticipants().size() < 2){
            return false;
        }

        if (dto.getParticipants().stream().filter(ParticipantDto::getIsExecutor).count() != 1){
            return false;
        }

        return dto.getParticipants().stream().mapToDouble(ParticipantDto::getPercentage).sum() == 1.0;
    }

    private boolean validateTotalBenefit(IpBasedContractCompositionDto contract) {
        if (contract.getParticipants() == null || contract.getParticipants().isEmpty()) {
            return false;
        }

        double total = contract.getParticipants().stream()
                .mapToDouble(ParticipantDto::getPercentage)
                .sum();

        return Math.abs(total - 1.0) < 0.0001; // Using small epsilon for double comparison
    }

    public static class InsertContractValidator implements ConstraintValidator<ValidIpBasedContractBatch.IpBasedContractInsertValidator, IpBasedContractCompositionDto> {
        @Override
        public boolean isValid(IpBasedContractCompositionDto request, ConstraintValidatorContext context ){
            DtoInsertChecking.NullInsertErrorDetails nullInsertErrorDetails = DtoInsertChecking
                    .isInsertRequestValid(request.getContractDetail().getClass(), request.getContractDetail());

            boolean isValid = nullInsertErrorDetails.isValid();

            if (!isValid){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(nullInsertErrorDetails.message())
                        .addPropertyNode("Null-fields:").addConstraintViolation();
            }

            return isValid;
        }
    }
}
