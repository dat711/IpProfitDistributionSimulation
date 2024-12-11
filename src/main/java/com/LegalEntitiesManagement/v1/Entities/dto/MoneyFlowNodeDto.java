package com.LegalEntitiesManagement.v1.Entities.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyFlowNodeDto {
    private MoneyNodeDto node;  // Can be either ContractNodeDto or StakeHolderLeafDto
    private List<ResponsibilityDto> incomingEdges = new ArrayList<>();
    private List<ResponsibilityDto> outgoingEdges = new ArrayList<>();
}
