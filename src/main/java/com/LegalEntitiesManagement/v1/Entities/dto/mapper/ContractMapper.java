package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractDto;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    ContractMapper INSTANCE = Mappers.getMapper(ContractMapper.class);


    @Mapping(source = "contractPriority", target = "contractPriority")
    @Mapping(source = "executor.id", target = "executorId")
    ContractDto toDto(Contract contract);

    @Mapping(source = "contractPriority", target = "contractPriority")
    @Mapping(source = "executorId", target = "executor.id")
    Contract toEntity(ContractDto contractDto);
    List<ContractDto> toDtoList(List<Contract> contracts);
    List<Contract> toEntityList(List<ContractDto> contractDtos);
}
