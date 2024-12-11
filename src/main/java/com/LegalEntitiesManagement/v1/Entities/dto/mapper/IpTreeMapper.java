package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.IpTreeDto;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IpTreeMapper {
    IpTreeMapper INSTANCE = Mappers.getMapper(IpTreeMapper.class);

    @Mapping(source = "intellectualProperty.id", target = "intellectualPropertyId")
    @Mapping(source = "rootContractNode.id", target = "rootContractNodeId")
    IpTreeDto toDto(IpTree ipTree);

    @Mapping(source = "intellectualPropertyId", target = "intellectualProperty.id")
    @Mapping(source = "rootContractNodeId", target = "rootContractNode.id")
    IpTree toEntity(IpTreeDto ipTreeDto);

    List<IpTreeDto> toDtoList(List<IpTree> ipTrees);
    List<IpTree> toEntityList(List<IpTreeDto> ipTreeDtos);
}
