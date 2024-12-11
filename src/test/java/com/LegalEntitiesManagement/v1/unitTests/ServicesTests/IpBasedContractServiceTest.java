package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpBasedContractRepository;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.IpBasedContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class IpBasedContractServiceTest extends BaseServiceTestMockedDependencies {
    private IpBasedContractService ipBasedContractService;
    private IpBasedContract testContract;
    private IpBasedContractDto testContractDto;
    private StakeHolder testExecutor;
    private Role testRole;
    private IntellectualProperty testIp;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        ipBasedContractService = new IpBasedContractService(ipBasedContractRepository);

        // Create test data
        testDate = LocalDate.now();

        // Create role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        // Create executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);

        // Create IP
        testIp = new IntellectualProperty("Test IP", "Test IP Description");
        testIp.setId(1L);

        // Create contract
        testContract = new IpBasedContract("Test Contract", testDate, 1, testIp, testExecutor);
        testContract.setId(1L);

        // Create DTO
        testContractDto = new IpBasedContractDto(1L, "Test Contract", testDate, 1, 1L, 1L);
    }

    @Test
    @DisplayName("Should find IpBasedContract by ID")
    void findById_ExistingId_ReturnsContract() {
        when(ipBasedContractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        IpBasedContract found = ipBasedContractService.findById(1L);

        assertNotNull(found);
        assertEquals(testContract.getId(), found.getId());
        assertEquals(testContract.getDescription(), found.getDescription());
        assertEquals(testContract.getIntellectualProperty().getId(), found.getIntellectualProperty().getId());
        assertEquals(testContract.getExecutor().getId(), found.getExecutor().getId());
        verify(ipBasedContractRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when IpBasedContract not found")
    void findById_NonExistingId_ThrowsException() {
        when(ipBasedContractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class, () -> ipBasedContractService.findById(99L));
        verify(ipBasedContractRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save IpBasedContract")
    void save_ValidContract_ReturnsSavedContract() {
        when(ipBasedContractRepository.save(any(IpBasedContract.class))).thenReturn(testContract);

        IpBasedContract saved = ipBasedContractService.save(testContract);

        assertNotNull(saved);
        assertEquals(testContract.getId(), saved.getId());
        assertEquals(testContract.getDescription(), saved.getDescription());
        assertEquals(testContract.getIntellectualProperty().getId(), saved.getIntellectualProperty().getId());
        assertEquals(testContract.getExecutor().getId(), saved.getExecutor().getId());
        verify(ipBasedContractRepository).save(testContract);
    }

    @Test
    @DisplayName("Should save IpBasedContract from DTO")
    void saveFromDto_ValidDto_ReturnsSavedContract() {
        when(ipBasedContractRepository.save(any(IpBasedContract.class))).thenReturn(testContract);

        IpBasedContract saved = ipBasedContractService.saveFromDto(testContractDto);

        assertNotNull(saved);
        assertEquals(testContractDto.getId(), saved.getId());
        assertEquals(testContractDto.getDescription(), saved.getDescription());
        assertEquals(testContractDto.getIpId(), saved.getIntellectualProperty().getId());
        assertEquals(testContractDto.getExecutorId(), saved.getExecutor().getId());
        verify(ipBasedContractRepository).save(any(IpBasedContract.class));
    }

    @Test
    @DisplayName("Should update IpBasedContract")
    void update_ValidContract_ReturnsUpdatedContract() {
        IpBasedContract updatedContract = new IpBasedContract("Updated Contract", testDate, 2, testIp, testExecutor);
        updatedContract.setId(1L);

        when(ipBasedContractRepository.save(any(IpBasedContract.class))).thenReturn(updatedContract);

        IpBasedContract result = ipBasedContractService.update(updatedContract);

        assertNotNull(result);
        assertEquals(updatedContract.getId(), result.getId());
        assertEquals(updatedContract.getDescription(), result.getDescription());
        assertEquals(updatedContract.getContractPriority(), result.getContractPriority());
        assertEquals(updatedContract.getIntellectualProperty().getId(), result.getIntellectualProperty().getId());
        verify(ipBasedContractRepository).save(updatedContract);
    }

    @Test
    @DisplayName("Should update IpBasedContract from DTO")
    void updateFromDto_ValidDto_ReturnsUpdatedContract() {
        // Create updated DTO and expected contract
        IpBasedContractDto updatedDto = new IpBasedContractDto(1L, "Updated Contract", testDate, 2, 1L, 1L);
        IpBasedContract updatedContract = new IpBasedContract("Updated Contract", testDate, 2, testIp, testExecutor);
        updatedContract.setId(1L);

        when(ipBasedContractRepository.save(any(IpBasedContract.class))).thenReturn(updatedContract);

        IpBasedContract result = ipBasedContractService.updateFromDto(updatedDto);

        assertNotNull(result);
        assertEquals(updatedDto.getId(), result.getId());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertEquals(updatedDto.getContractPriority(), result.getContractPriority());
        assertEquals(updatedDto.getIpId(), result.getIntellectualProperty().getId());
        assertEquals(updatedDto.getExecutorId(), result.getExecutor().getId());
        verify(ipBasedContractRepository).save(any(IpBasedContract.class));
    }

    @Test
    @DisplayName("Should find contracts by IP ID")
    void findContractsByIpId_ReturnsContracts() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(testContract));
        when(ipBasedContractRepository.getIpBasedContractByIpId(1L)).thenReturn(contracts);

        Set<IpBasedContract> found = ipBasedContractService.findContractsByIpId(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testContract));
        verify(ipBasedContractRepository).getIpBasedContractByIpId(1L);
    }

    @Test
    @DisplayName("Should find IP-based contracts by stakeholder ID")
    void findIpBasedContractsByStakeholderId_ReturnsContracts() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(testContract));
        when(ipBasedContractRepository.findIpBasedContractsByStakeholderId(1L)).thenReturn(contracts);

        Set<IpBasedContract> found = ipBasedContractService.findIpBasedContractsByStakeholderId(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testContract));
        verify(ipBasedContractRepository).findIpBasedContractsByStakeholderId(1L);
    }

    @Test
    @DisplayName("Should find IP-based contracts where stakeholder is executor")
    void findIpBasedContractsWhereStakeholderIsExecutor_ReturnsContracts() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(testContract));
        when(ipBasedContractRepository.findIpBasedContractsWhereStakeholderIsExecutor(1L)).thenReturn(contracts);

        Set<IpBasedContract> found = ipBasedContractService.findIpBasedContractsWhereStakeholderIsExecutor(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testContract));
        verify(ipBasedContractRepository).findIpBasedContractsWhereStakeholderIsExecutor(1L);
    }

    @Test
    @DisplayName("Should verify existing IP-based contract")
    void verify_ExistingContract_NoException() {
        when(ipBasedContractRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> ipBasedContractService.verify(testContract));
        verify(ipBasedContractRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing contract during verification")
    void verify_NonExistingContract_ThrowsException() {
        when(ipBasedContractRepository.existsById(1L)).thenReturn(false);

        assertThrows(ContractNotFoundException.class, () -> ipBasedContractService.verify(testContract));
        verify(ipBasedContractRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should find all IP-based contracts")
    void findAll_ReturnsAllContracts() {
        IpBasedContract contract2 = new IpBasedContract("Test Contract 2", testDate, 2, testIp, testExecutor);
        contract2.setId(2L);
        List<IpBasedContract> contracts = Arrays.asList(testContract, contract2);

        when(ipBasedContractRepository.findAll()).thenReturn(contracts);

        List<IpBasedContract> result = ipBasedContractService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ipBasedContractRepository).findAll();
    }
}
