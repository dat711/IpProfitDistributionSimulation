package com.LegalEntitiesManagement.v1.Entities.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpBasedContractCompositeDto {
    private IpBasedContractDto contract;
    private Set<ContractParticipantDto> participants;
}
