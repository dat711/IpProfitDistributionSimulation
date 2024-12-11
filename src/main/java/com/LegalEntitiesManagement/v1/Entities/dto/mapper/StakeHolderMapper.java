package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;

import java.util.List;
@Mapper(componentModel = "spring")
public interface StakeHolderMapper {
    StakeHolderMapper INSTANCE = Mappers.getMapper(StakeHolderMapper.class);

    @Mapping(source = "role.id", target = "roleId")
    StakeHolderDto toDto(StakeHolder stakeHolder);

    @Mapping(source = "roleId", target = "role.id")
    StakeHolder toEntity(StakeHolderDto stakeHolderDto);

    List<StakeHolderDto> toDtoList(List<StakeHolder> roles);

    List<StakeHolder> toEntityList(List<StakeHolderDto> stakeHolderDtos);
}
