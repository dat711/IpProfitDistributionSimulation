package com.LegalEntitiesManagement.v1.Entities.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Composite DTO that combines ContractDto with its participants
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractCompositeDto {
    private ContractDto contract;
    private Set<ContractParticipantDto> participants;
}
