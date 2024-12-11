package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractParticipantMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContractParticipant Mapper Tests")
public class ContractParticipantMapperTest {
    private final ContractParticipantMapper participantMapper = ContractParticipantMapper.INSTANCE;
    private Contract contract;
    private StakeHolder stakeHolder;
    private ContractParticipant participant;
    private ContractParticipantDto participantDto;

    @BeforeEach
    void setUp() {
        // Setup Contract
        contract = new Contract();
        contract.setId(1L);
        contract.setDescription("Test Contract");

        // Setup StakeHolder
        stakeHolder = new StakeHolder();
        stakeHolder.setId(1L);
        stakeHolder.setName("Test Stakeholder");

        // Setup ContractParticipant
        participant = new ContractParticipant(contract, 50.0, true, stakeHolder);
        participant.setId(1L);

        // Setup DTO
        participantDto = new ContractParticipantDto(1L, 1L, 1L, 50.0, true);
    }

    @Test
    @DisplayName("Should map ContractParticipant to DTO")
    void shouldMapContractParticipantToDto() {
        // when
        ContractParticipantDto dto = participantMapper.toDto(participant);

        // then
        assertNotNull(dto);
        assertEquals(participant.getId(), dto.getId());
        assertEquals(participant.getContract().getId(), dto.getContractId());
        assertEquals(participant.getStakeholder().getId(), dto.getStakeholderId());
        assertEquals(participant.getPercentage(), dto.getPercentage());
        assertEquals(participant.getIsExecutor(), dto.getIsExecutor());
    }

    @Test
    @DisplayName("Should map DTO to ContractParticipant")
    void shouldMapDtoToContractParticipant() {
        // when
        ContractParticipant entity = participantMapper.toEntity(participantDto);

        // then
        assertNotNull(entity);
        assertEquals(participantDto.getId(), entity.getId());
        assertEquals(participantDto.getContractId(), entity.getContract().getId());
        assertEquals(participantDto.getStakeholderId(), entity.getStakeholder().getId());
        assertEquals(participantDto.getPercentage(), entity.getPercentage());
        assertEquals(participantDto.getIsExecutor(), entity.getIsExecutor());
    }

    @Test
    @DisplayName("Should map ContractParticipant list to DTO list")
    void shouldMapContractParticipantListToDtoList() {
        // given
        ContractParticipant participant2 = new ContractParticipant(contract, 30.0, false, stakeHolder);
        participant2.setId(2L);
        List<ContractParticipant> participants = Arrays.asList(participant, participant2);

        // when
        List<ContractParticipantDto> dtos = participantMapper.toDtoList(participants);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(participants.get(0).getId(), dtos.get(0).getId());
        assertEquals(participants.get(1).getId(), dtos.get(1).getId());
        assertEquals(participants.get(0).getPercentage(), dtos.get(0).getPercentage());
        assertEquals(participants.get(1).getPercentage(), dtos.get(1).getPercentage());
    }

    @Test
    @DisplayName("Should map DTO list to ContractParticipant list")
    void shouldMapDtoListToContractParticipantList() {
        // given
        ContractParticipantDto dto2 = new ContractParticipantDto(2L, 1L, 1L, 30.0, false);
        List<ContractParticipantDto> dtos = Arrays.asList(participantDto, dto2);

        // when
        List<ContractParticipant> participants = participantMapper.toEntityList(dtos);

        // then
        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertEquals(dtos.get(0).getId(), participants.get(0).getId());
        assertEquals(dtos.get(1).getId(), participants.get(1).getId());
        assertEquals(dtos.get(0).getPercentage(), participants.get(0).getPercentage());
        assertEquals(dtos.get(1).getPercentage(), participants.get(1).getPercentage());
    }
}
