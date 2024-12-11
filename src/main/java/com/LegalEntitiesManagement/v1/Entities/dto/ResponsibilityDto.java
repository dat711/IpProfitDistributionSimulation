package com.LegalEntitiesManagement.v1.Entities.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityDto {
    private Long id;
    private Long targetNodeId;
    private Long sourceNodeId;
    private double percentage;
}
