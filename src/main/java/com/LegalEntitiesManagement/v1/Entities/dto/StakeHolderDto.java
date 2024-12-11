package com.LegalEntitiesManagement.v1.Entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StakeHolderDto {
    private Long id;
    private String name;
    private Long roleId;
}
