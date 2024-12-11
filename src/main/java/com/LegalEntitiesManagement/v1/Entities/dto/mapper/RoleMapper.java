package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    RoleDto toDto(Role role);

    // Convert DTO to Entity
    Role toEntity(RoleDto roleDto);

    // Convert List of Entities to List of DTOs
    List<RoleDto> toDtoList(List<Role> roles);

    // Convert List of DTOs to List of Entities
    List<Role> toEntityList(List<RoleDto> roleDtos);

    // Update existing entity with DTO values
    void updateEntityFromDto(RoleDto roleDto, @MappingTarget Role role);
}
