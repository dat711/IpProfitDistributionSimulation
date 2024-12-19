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
    @NotNull(message = "The contract must be specified")
    ContractDto contractDetail;

    @NotNull(message = "The participants must be specified")
    @ValidTotalBenefit
    Set<ParticipantDto> participants;
}
