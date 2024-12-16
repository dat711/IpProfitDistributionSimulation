package com.LegalEntitiesManagement.v1.Entities.dto.mapper;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    ParticipantMapper INSTANCE = Mappers.getMapper(ParticipantMapper.class);

    @Mapping(source = "stakeholder.id", target = "stakeholderId")
    ParticipantDto toDto(ContractParticipant participant);

    @Mapping(source = "stakeholderId", target = "stakeholder.id")
    ContractParticipant toEntity(ParticipantDto participantDto);

    List<ParticipantDto> toDtoList(List<ContractParticipant> participants);
    List<ContractParticipant> toEntityList(List<ParticipantDto> participantDtos);

    Set<ParticipantDto> toDtoSet(Set<ContractParticipant> participants);
    Set<ContractParticipant> toEntitySet(Set<ParticipantDto> participantDtos);
}
