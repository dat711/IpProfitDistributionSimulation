package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.BatchValidation;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;
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
    @NotNull(message = "The contract must be specified", groups = {SingleValidation.class, BatchValidation.class})
    IpBasedContractDto contractDto;

    @NotNull(message = "The participants must be specified")
    @ValidTotalBenefit(groups = {SingleValidation.class})
    Set<ParticipantDto> participants;
}
