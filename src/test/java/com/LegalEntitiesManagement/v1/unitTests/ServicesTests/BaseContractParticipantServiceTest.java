package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractParticipantNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.BaseContractParticipantService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.StakeHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class BaseContractParticipantServiceTest extends BaseServiceTestMockedDependencies {
    private BaseContractParticipantService participantService;
    @Mock
    private StakeHolderService stakeHolderService;

    private ContractParticipant testParticipant;
    private ContractParticipantDto testParticipantDto;
    private Contract testContract;
    private StakeHolder testStakeHolder;
    private StakeHolder testExecutor;
    private Role testRole;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        participantService = new BaseContractParticipantService(contractParticipantRepository, stakeHolderService);

        // Create test role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        // Create stakeholders
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);
        testStakeHolder = new StakeHolder("Test Participant", testRole);
        testStakeHolder.setId(2L);

        // Create contract
        testContract = new Contract("Test Contract", LocalDate.now(), 1, testExecutor);
        testContract.setId(1L);

        // Create participant
        testParticipant = new ContractParticipant(testContract, 25.0, false, testStakeHolder);
        testParticipant.setId(1L);

        // Create DTO
        testParticipantDto = new ContractParticipantDto(1L, 1L, 2L, 25.0, false);
    }

    @Test
    @DisplayName("Should find ContractParticipant by ID")
    void findById_ExistingId_ReturnsParticipant() {
        when(contractParticipantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));

        ContractParticipant found = participantService.findById(1L);

        assertNotNull(found);
        assertEquals(testParticipant.getId(), found.getId());
        assertEquals(testParticipant.getContract().getId(), found.getContract().getId());
        assertEquals(testParticipant.getStakeholder().getId(), found.getStakeholder().getId());
        assertEquals(testParticipant.getPercentage(), found.getPercentage());
        assertEquals(testParticipant.getIsExecutor(), found.getIsExecutor());
        verify(contractParticipantRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when ContractParticipant not found")
    void findById_NonExistingId_ThrowsException() {
        when(contractParticipantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContractParticipantNotFoundException.class, () -> participantService.findById(99L));
        verify(contractParticipantRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save ContractParticipant")
    void save_ValidParticipant_ReturnsSavedParticipant() {
        when(contractParticipantRepository.save(any(ContractParticipant.class))).thenReturn(testParticipant);

        ContractParticipant saved = participantService.save(testParticipant);

        assertNotNull(saved);
        assertEquals(testParticipant.getId(), saved.getId());
        assertEquals(testParticipant.getContract().getId(), saved.getContract().getId());
        assertEquals(testParticipant.getStakeholder().getId(), saved.getStakeholder().getId());
        assertEquals(testParticipant.getPercentage(), saved.getPercentage());
        assertEquals(testParticipant.getIsExecutor(), saved.getIsExecutor());
        verify(contractParticipantRepository).save(testParticipant);
    }

    @Test
    @DisplayName("Should save ContractParticipant from DTO")
    void saveFromDto_ValidDto_ReturnsSavedParticipant() {
        when(contractParticipantRepository.save(any(ContractParticipant.class))).thenReturn(testParticipant);

        ContractParticipant saved = participantService.saveFromDto(testParticipantDto);

        assertNotNull(saved);
        assertEquals(testParticipantDto.getId(), saved.getId());
        assertEquals(testParticipantDto.getStakeholderId(), saved.getStakeholder().getId());
        assertEquals(testParticipantDto.getPercentage(), saved.getPercentage());
        assertEquals(testParticipantDto.getIsExecutor(), saved.getIsExecutor());
        verify(stakeHolderService).findById(testParticipantDto.getStakeholderId());
        verify(contractParticipantRepository).save(any(ContractParticipant.class));
    }

    @Test
    @DisplayName("Should find participants by contract ID")
    void findParticipantsByContractId_ReturnsParticipants() {
        Set<ContractParticipant> participants = new HashSet<>(Arrays.asList(testParticipant));
        when(contractParticipantRepository.findParticipantsByContractId(1L)).thenReturn(participants);

        Set<ContractParticipant> found = participantService.findParticipantsByContractId(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testParticipant));
        verify(contractParticipantRepository).findParticipantsByContractId(1L);
    }

    @Test
    @DisplayName("Should check if stakeholder is participant in contract")
    void isStakeholderParticipantInContract_ReturnsBoolean() {
        when(contractParticipantRepository.isStakeholderParticipantInContract(1L, 2L)).thenReturn(true);

        boolean isParticipant = participantService.isStakeholderParticipantInContract(1L, 2L);

        assertTrue(isParticipant);
        verify(contractParticipantRepository).isStakeholderParticipantInContract(1L, 2L);
    }

    @Test
    @DisplayName("Should find executor by contract ID")
    void findExecutorByContractId_ReturnsExecutor() {
        ContractParticipant executorParticipant = new ContractParticipant(testContract, 25.0, true, testExecutor);
        executorParticipant.setId(2L);

        when(contractParticipantRepository.findExecutorByContractId(1L)).thenReturn(Optional.of(executorParticipant));

        ContractParticipant found = participantService.findExecutorByContractId(1L);

        assertNotNull(found);
        assertTrue(found.getIsExecutor());
        assertEquals(testExecutor.getId(), found.getStakeholder().getId());
        verify(contractParticipantRepository).findExecutorByContractId(1L);
    }

    @Test
    @DisplayName("Should throw exception when executor not found")
    void findExecutorByContractId_ThrowsException() {
        when(contractParticipantRepository.findExecutorByContractId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> participantService.findExecutorByContractId(1L));
        verify(contractParticipantRepository).findExecutorByContractId(1L);
    }

    @Test
    @DisplayName("Should verify existing participant")
    void verify_ExistingParticipant_NoException() {
        when(contractParticipantRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> participantService.verify(testParticipant));
        verify(contractParticipantRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing participant during verification")
    void verify_NonExistingParticipant_ThrowsException() {
        when(contractParticipantRepository.existsById(1L)).thenReturn(false);

        assertThrows(ContractParticipantNotFoundException.class, () -> participantService.verify(testParticipant));
        verify(contractParticipantRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for invalid type during verification")
    void verify_InvalidType_ThrowsException() {
        String invalidObject = "Invalid Type";

        assertThrows(TypeNotMatchException.class, () -> participantService.verify(invalidObject));
    }

    @Test
    @DisplayName("Should find all participants")
    void findAll_ReturnsAllParticipants() {
        ContractParticipant participant2 = new ContractParticipant(testContract, 30.0, false, testStakeHolder);
        participant2.setId(2L);
        List<ContractParticipant> participants = Arrays.asList(testParticipant, participant2);

        when(contractParticipantRepository.findAll()).thenReturn(participants);

        List<ContractParticipant> result = participantService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contractParticipantRepository).findAll();
    }
}
