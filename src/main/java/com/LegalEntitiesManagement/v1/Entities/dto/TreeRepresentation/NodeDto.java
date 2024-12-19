package com.LegalEntitiesManagement.v1.Entities.dto.TreeRepresentation;

import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;

import java.util.List;

public record NodeDto(String contractLink, IpBasedContractDto ipBasedContractDto, List<DownStreamReceiver> receivers,
                      List<DownStreamNodeDetail> downStreamContracts) {}
