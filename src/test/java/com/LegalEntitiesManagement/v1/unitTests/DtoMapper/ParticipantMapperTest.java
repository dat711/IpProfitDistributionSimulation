package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ParticipantMapper;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class ParticipantMapperTest {
    private final ParticipantMapper participantMapper = ParticipantMapper.INSTANCE;

    @Test
    void shouldMapContractParticipantToDto() {
        // given
        StakeHolder stakeholder = new StakeHolder();
        stakeholder.setId(1L);
        stakeholder.setName("Test Stakeholder");

        ContractParticipant participant = new ContractParticipant();
        participant.setId(1L);
        participant.setPercentage(25.0);
        participant.setIsExecutor(true);
        participant.setStakeholder(stakeholder);

        // when
        ParticipantDto dto = participantMapper.toDto(participant);

        // then
        assertNotNull(dto);
        assertEquals(participant.getPercentage(), dto.getPercentage());
        assertEquals(participant.getIsExecutor(), dto.getIsExecutor());
        assertEquals(stakeholder.getId(), dto.getStakeholderId());
    }

    @Test
    void shouldMapDtoToContractParticipant() {
        // given
        ParticipantDto dto = new ParticipantDto();
        dto.setStakeholderId(1L);
        dto.setPercentage(25.0);
        dto.setIsExecutor(true);

        // when
        ContractParticipant participant = participantMapper.toEntity(dto);

        // then
        assertNotNull(participant);
        assertEquals(dto.getPercentage(), participant.getPercentage());
        assertEquals(dto.getIsExecutor(), participant.getIsExecutor());
        assertEquals(dto.getStakeholderId(), participant.getStakeholder().getId());
    }

    @Test
    void shouldMapParticipantListToDtoList() {
        // given
        StakeHolder stakeholder1 = new StakeHolder();
        stakeholder1.setId(1L);

        StakeHolder stakeholder2 = new StakeHolder();
        stakeholder2.setId(2L);

        ContractParticipant participant1 = new ContractParticipant();
        participant1.setId(1L);
        participant1.setPercentage(25.0);
        participant1.setIsExecutor(true);
        participant1.setStakeholder(stakeholder1);

        ContractParticipant participant2 = new ContractParticipant();
        participant2.setId(2L);
        participant2.setPercentage(75.0);
        participant2.setIsExecutor(false);
        participant2.setStakeholder(stakeholder2);

        List<ContractParticipant> participants = Arrays.asList(participant1, participant2);

        // when
        List<ParticipantDto> dtos = participantMapper.toDtoList(participants);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(participant1.getPercentage(), dtos.get(0).getPercentage());
        assertEquals(participant2.getPercentage(), dtos.get(1).getPercentage());
        assertEquals(stakeholder1.getId(), dtos.get(0).getStakeholderId());
        assertEquals(stakeholder2.getId(), dtos.get(1).getStakeholderId());
    }

    @Test
    void shouldMapDtoListToParticipantList() {
        // given
        ParticipantDto dto1 = new ParticipantDto( 1L, 25.0, true);
        ParticipantDto dto2 = new ParticipantDto( 2L, 75.0, false);
        List<ParticipantDto> dtos = Arrays.asList(dto1, dto2);

        // when
        List<ContractParticipant> participants = participantMapper.toEntityList(dtos);

        // then
        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertEquals(dto1.getPercentage(), participants.get(0).getPercentage());
        assertEquals(dto2.getPercentage(), participants.get(1).getPercentage());
        assertEquals(dto1.getStakeholderId(), participants.get(0).getStakeholder().getId());
        assertEquals(dto2.getStakeholderId(), participants.get(1).getStakeholder().getId());
    }

    @Test
    void shouldMapParticipantSetToDtoSet() {
        // given
        StakeHolder stakeholder1 = new StakeHolder();
        stakeholder1.setId(1L);

        StakeHolder stakeholder2 = new StakeHolder();
        stakeholder2.setId(2L);

        ContractParticipant participant1 = new ContractParticipant();
        participant1.setId(1L);
        participant1.setPercentage(25.0);
        participant1.setIsExecutor(true);
        participant1.setStakeholder(stakeholder1);

        ContractParticipant participant2 = new ContractParticipant();
        participant2.setId(2L);
        participant2.setPercentage(75.0);
        participant2.setIsExecutor(false);
        participant2.setStakeholder(stakeholder2);

        Set<ContractParticipant> participants = new HashSet<>(Arrays.asList(participant1, participant2));

        // when
        Set<ParticipantDto> dtos = participantMapper.toDtoSet(participants);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(dto -> dto.getPercentage().equals(25.0)));
        assertTrue(dtos.stream().anyMatch(dto -> dto.getPercentage().equals(75.0)));
    }

    @Test
    void shouldMapDtoSetToParticipantSet() {
        // given
        ParticipantDto dto1 = new ParticipantDto( 1L, 25.0, true);
        ParticipantDto dto2 = new ParticipantDto( 2L, 75.0, false);
        Set<ParticipantDto> dtos = new HashSet<>(Arrays.asList(dto1, dto2));

        // when
        Set<ContractParticipant> participants = participantMapper.toEntitySet(dtos);

        // then
        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertTrue(participants.stream().anyMatch(p -> p.getPercentage().equals(25.0)));
        assertTrue(participants.stream().anyMatch(p -> p.getPercentage().equals(75.0)));
    }
}
