package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IpBasedContractMapper {
    IpBasedContractMapper INSTANCE = Mappers.getMapper(IpBasedContractMapper.class);

    @Mapping(source = "intellectualProperty.id", target = "ipId")
    @Mapping(source = "contractPriority", target = "contractPriority")
    @Mapping(source = "executor.id", target = "executorId")
    IpBasedContractDto toDto(IpBasedContract contract);

    @Mapping(source = "ipId", target = "intellectualProperty.id")
    @Mapping(source = "contractPriority", target = "contractPriority")
    @Mapping(source = "executorId", target = "executor.id")
    IpBasedContract toEntity(IpBasedContractDto contractDto);

    List<IpBasedContractDto> toDtoList(List<IpBasedContract> contracts);
    List<IpBasedContract> toEntityList(List<IpBasedContractDto> contractDtos);
}
