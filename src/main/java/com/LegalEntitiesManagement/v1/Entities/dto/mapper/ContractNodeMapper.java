package com.LegalEntitiesManagement.v1.Entities.dto.mapper;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractNodeDto;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContractNodeMapper {
    ContractNodeMapper INSTANCE = Mappers.getMapper(ContractNodeMapper.class);

    @Mapping(source = "contract.id", target = "contractId")
    ContractNodeDto toDto(ContractNode contractNode);

    @Mapping(source = "contractId", target = "contract.id")
    ContractNode toEntity(ContractNodeDto contractNodeDto);

    List<ContractNodeDto> toDtoList(List<ContractNode> contractNodes);
    List<ContractNode> toEntityList(List<ContractNodeDto> contractNodeDtos);
}
