package com.LegalEntitiesManagement.v1.Entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractParticipantDto {
    private Long id;
    private Long contractId;
    private Long stakeholderId;
    private Double percentage;
    private Boolean isExecutor;

    public ContractParticipantDto(Long contractId, Long stakeholderId, Double percentage, Boolean isExecutor) {
        this.contractId = contractId;
        this.stakeholderId = stakeholderId;
        this.percentage = percentage;
        this.isExecutor = isExecutor;
    }

    public ContractParticipantDto(Long stakeholderId, Double percentage, Boolean isExecutor) {
        this.stakeholderId = stakeholderId;
        this.percentage = percentage;
        this.isExecutor = isExecutor;
    }
}


