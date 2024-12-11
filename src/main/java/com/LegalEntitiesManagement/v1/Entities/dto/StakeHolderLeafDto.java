package com.LegalEntitiesManagement.v1.Entities.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StakeHolderLeafDto extends MoneyNodeDto {
    private Long stakeholderId;

    public StakeHolderLeafDto(Long id, Long stakeholderId) {
        super(id);
        this.stakeholderId = stakeholderId;
    }
}
