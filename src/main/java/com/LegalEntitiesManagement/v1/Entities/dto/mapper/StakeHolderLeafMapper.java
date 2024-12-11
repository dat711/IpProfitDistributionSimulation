package com.LegalEntitiesManagement.v1.Entities.dto.mapper;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderLeafDto;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StakeHolderLeafMapper {
    StakeHolderLeafMapper INSTANCE = Mappers.getMapper(StakeHolderLeafMapper.class);

    @Mapping(source = "stakeHolder.id", target = "stakeholderId")
    StakeHolderLeafDto toDto(StakeHolderLeaf stakeHolderLeaf);

    @Mapping(source = "stakeholderId", target = "stakeHolder.id")
    StakeHolderLeaf toEntity(StakeHolderLeafDto stakeHolderLeafDto);

    List<StakeHolderLeafDto> toDtoList(List<StakeHolderLeaf> stakeHolderLeaves);
    List<StakeHolderLeaf> toEntityList(List<StakeHolderLeafDto> stakeHolderLeafDtos);
}


