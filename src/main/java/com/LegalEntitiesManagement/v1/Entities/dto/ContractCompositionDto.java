package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidTotalBenefit;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ContractCompositionDto {
    @NotNull
    ContractDto contractDto;

    @ValidTotalBenefit
    Set<ParticipantDto> participants;
}
