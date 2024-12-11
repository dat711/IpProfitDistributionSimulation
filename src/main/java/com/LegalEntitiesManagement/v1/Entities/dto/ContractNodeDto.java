package com.LegalEntitiesManagement.v1.Entities.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContractNodeDto extends MoneyNodeDto {
    private Long contractId;

    public ContractNodeDto(Long id, Long contractId) {
        super(id);
        this.contractId = contractId;
    }
}
