package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.BaseContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class BaseContractServiceTest extends BaseServiceTestMockedDependencies {
    private BaseContractService contractService;
    private Contract testContract;
    private ContractDto testContractDto;
    private StakeHolder testExecutor;
    private Role testRole;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        contractService = new BaseContractService(contractRepository);

        // Create test data
        testDate = LocalDate.now();

        // Create role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        // Create executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);

        // Create contract
        testContract = new Contract("Test Contract", testDate, 1, testExecutor);
        testContract.setId(1L);

        // Create DTO
        testContractDto = new ContractDto(1L, "Test Contract", testDate, 1, 1L);
    }

    @Test
    @DisplayName("Should find Contract by ID")
    void findById_ExistingId_ReturnsContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        Contract found = contractService.findById(1L);

        assertNotNull(found);
        assertEquals(testContract.getId(), found.getId());
        assertEquals(testContract.getDescription(), found.getDescription());
        assertEquals(testContract.getContractActiveDate(), found.getContractActiveDate());
        assertEquals(testContract.getExecutor().getId(), found.getExecutor().getId());
        verify(contractRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when Contract not found")
    void findById_NonExistingId_ThrowsException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class, () -> contractService.findById(99L));
        verify(contractRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save Contract")
    void save_ValidContract_ReturnsSavedContract() {
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        Contract saved = contractService.save(testContract);

        assertNotNull(saved);
        assertEquals(testContract.getId(), saved.getId());
        assertEquals(testContract.getDescription(), saved.getDescription());
        assertEquals(testContract.getContractActiveDate(), saved.getContractActiveDate());
        assertEquals(testContract.getExecutor().getId(), saved.getExecutor().getId());
        verify(contractRepository).save(testContract);
    }

    @Test
    @DisplayName("Should save Contract from DTO")
    void saveFromDto_ValidDto_ReturnsSavedContract() {
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        Contract saved = contractService.saveFromDto(testContractDto);

        assertNotNull(saved);
        assertEquals(testContractDto.getId(), saved.getId());
        assertEquals(testContractDto.getDescription(), saved.getDescription());
        assertEquals(testContractDto.getContractActiveDate(), saved.getContractActiveDate());
        assertEquals(testContractDto.getExecutorId(), saved.getExecutor().getId());
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should update Contract")
    void update_ValidContract_ReturnsUpdatedContract() {
        Contract updatedContract = new Contract("Updated Contract", testDate, 2, testExecutor);
        updatedContract.setId(1L);

        when(contractRepository.save(any(Contract.class))).thenReturn(updatedContract);

        Contract result = contractService.update(updatedContract);

        assertNotNull(result);
        assertEquals(updatedContract.getId(), result.getId());
        assertEquals(updatedContract.getDescription(), result.getDescription());
        assertEquals(updatedContract.getContractPriority(), result.getContractPriority());
        verify(contractRepository).save(updatedContract);
    }

    @Test
    @DisplayName("Should update Contract from DTO")
    void updateFromDto_ValidDto_ReturnsUpdatedContract() {
        // Create updated test data
        ContractDto updatedDto = new ContractDto(1L, "Updated Contract", testDate, 2, 1L);

        // Create expected updated contract to match the DTO
        Contract updatedContract = new Contract("Updated Contract", testDate, 2, testExecutor);
        updatedContract.setId(1L);

        // Mock the repository to return our updated contract
        when(contractRepository.save(any(Contract.class))).thenReturn(updatedContract);

        Contract result = contractService.updateFromDto(updatedDto);

        assertNotNull(result);
        assertEquals(updatedDto.getId(), result.getId());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertEquals(updatedDto.getContractPriority(), result.getContractPriority());
        assertEquals(updatedDto.getContractActiveDate(), result.getContractActiveDate());
        assertEquals(updatedDto.getExecutorId(), result.getExecutor().getId());
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should find contracts by stakeholder ID")
    void findContractsParticipatedByStakeHolder_ReturnsContracts() {
        Set<Contract> contracts = new HashSet<>(Arrays.asList(testContract));
        when(contractRepository.findContractsByStakeholderId(1L)).thenReturn(contracts);

        Set<Contract> found = contractService.findContractsParticipatedByStakeHolder(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testContract));
        verify(contractRepository).findContractsByStakeholderId(1L);
    }

    @Test
    @DisplayName("Should find contracts where stakeholder is executor")
    void findContractsWhereStakeholderIsExecutor_ReturnsContracts() {
        Set<Contract> contracts = new HashSet<>(Arrays.asList(testContract));
        when(contractRepository.findContractsWhereStakeholderIsExecutor(1L)).thenReturn(contracts);

        Set<Contract> found = contractService.findContractsWhereStakeholderIsExecutor(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testContract));
        verify(contractRepository).findContractsWhereStakeholderIsExecutor(1L);
    }

    @Test
    @DisplayName("Should verify existing contract")
    void verify_ExistingContract_NoException() {
        when(contractRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> contractService.verify(testContract));
        verify(contractRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing contract during verification")
    void verify_NonExistingContract_ThrowsException() {
        when(contractRepository.existsById(1L)).thenReturn(false);

        assertThrows(ContractNotFoundException.class, () -> contractService.verify(testContract));
        verify(contractRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for invalid type during verification")
    void verify_InvalidType_ThrowsException() {
        String invalidObject = "Invalid Type";

        assertThrows(TypeNotMatchException.class, () -> contractService.verify(invalidObject));
    }

    @Test
    @DisplayName("Should find all contracts")
    void findAll_ReturnsAllContracts() {
        Contract contract2 = new Contract("Test Contract 2", testDate, 2, testExecutor);
        contract2.setId(2L);
        List<Contract> contracts = Arrays.asList(testContract, contract2);

        when(contractRepository.findAll()).thenReturn(contracts);

        List<Contract> result = contractService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contractRepository).findAll();
    }
}
