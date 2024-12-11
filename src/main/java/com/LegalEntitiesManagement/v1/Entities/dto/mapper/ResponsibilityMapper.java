package com.LegalEntitiesManagement.v1.Entities.dto.mapper;
import com.LegalEntitiesManagement.v1.Entities.dto.ResponsibilityDto;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponsibilityMapper {
    ResponsibilityMapper INSTANCE = Mappers.getMapper(ResponsibilityMapper.class);

    @Mapping(source = "target.id", target = "targetNodeId")
    @Mapping(source = "source.id", target = "sourceNodeId")
    ResponsibilityDto toDto(Responsibility responsibility);

    @Mapping(source = "targetNodeId", target = "target.id")
    @Mapping(source = "sourceNodeId", target = "source.id")
    Responsibility toEntity(ResponsibilityDto responsibilityDto);

    List<ResponsibilityDto> toDtoList(List<Responsibility> responsibilities);
    List<Responsibility> toEntityList(List<ResponsibilityDto> responsibilityDtos);
}
