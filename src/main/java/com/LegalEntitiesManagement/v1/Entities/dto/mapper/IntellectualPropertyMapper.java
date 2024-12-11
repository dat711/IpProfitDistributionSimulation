package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IntellectualPropertyMapper {
    IntellectualPropertyMapper INSTANCE = Mappers.getMapper(IntellectualPropertyMapper.class);

    IntellectualPropertyDto toDto(IntellectualProperty intellectualProperty);

    // Convert DTO to Entity
    IntellectualProperty toEntity(IntellectualPropertyDto intellectualPropertyDto);

    // Convert List of Entities to List of DTOs
    List<IntellectualPropertyDto> toDtoList(List<IntellectualProperty> intellectualProperties);

    // Convert List of DTOs to List of Entities
    List<IntellectualProperty> toEntityList(List<IntellectualPropertyDto> intellectualPropertyDtos);
}
