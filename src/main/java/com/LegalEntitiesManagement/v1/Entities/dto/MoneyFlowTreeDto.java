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
public class MoneyFlowTreeDto {
    private IpTreeDto treeInfo;
    private MoneyFlowNodeDto rootNode;
    private List<MoneyFlowNodeDto> allNodes = new ArrayList<>();
}
