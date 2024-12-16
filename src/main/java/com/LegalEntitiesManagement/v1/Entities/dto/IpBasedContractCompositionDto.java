package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidTotalBenefit;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IpBasedContractCompositionDto {
    @NotNull
    IpBasedContractDto contractDto;

    @ValidTotalBenefit
    Set<ParticipantDto> participants;
}
