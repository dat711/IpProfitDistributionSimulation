package com.LegalEntitiesManagement.v1.Entities.dto.mapper;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ContractParticipantMapper {

    ContractParticipantMapper INSTANCE = Mappers.getMapper(ContractParticipantMapper.class);

    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "stakeholder.id", target = "stakeholderId")
    @Mapping(source = "percentage", target = "percentage")
    ContractParticipantDto toDto(ContractParticipant contractParticipant);

    @Mapping(source = "contractId", target = "contract.id")
    @Mapping(source = "stakeholderId", target = "stakeholder.id")
    @Mapping(source = "percentage", target = "percentage")
    ContractParticipant toEntity(ContractParticipantDto contractParticipantDto);

    List<ContractParticipantDto> toDtoList(List<ContractParticipant> contractParticipants);
    List<ContractParticipant> toEntityList(List<ContractParticipantDto> contractParticipantDtos);
}
