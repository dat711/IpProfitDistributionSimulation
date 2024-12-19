package com.LegalEntitiesManagement.v1.Entities.dto.TreeRepresentation;

import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;

public record DownStreamReceiver(String Link, StakeHolderDto stakeHolderInfo, Double percentage) {
}
