package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpBasedContractRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.IntellectualPropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
public class IpBasedContractRepositoryTest extends BaseRepositoryTestProperties  {
    @Autowired
    private IpBasedContractRepository ipBasedContractRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    @Autowired
    private IntellectualPropertyRepository intellectualPropertyRepository;

    private Role testRole;
    private StakeHolder testExecutor;
    private IntellectualProperty testIp;
    private IpBasedContract testContract;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);

        // Create test IP
        testIp = new IntellectualProperty("Test IP", "Test IP Description");
        testIp = intellectualPropertyRepository.save(testIp);

        // Create test IP-based contract
        testContract = new IpBasedContract(
                "Test IP Contract",
                LocalDate.now(),
                1,
                testIp,
                testExecutor
        );
    }

    @Test
    @Order(1)
    void testIpBasedContractInsertionFunction() {
        IpBasedContract savedContract = ipBasedContractRepository.save(testContract);
        assertNotNull(savedContract.getId());
        assertEquals("Test IP Contract", savedContract.getDescription());
        assertEquals(testExecutor.getId(), savedContract.getExecutor().getId());
        assertEquals(testIp.getId(), savedContract.getIntellectualProperty().getId());
    }

    @Test
    @Order(2)
    void testIpBasedContractExistFunction() {
        IpBasedContract savedContract = ipBasedContractRepository.save(testContract);
        assertTrue(ipBasedContractRepository.existsById(savedContract.getId()));
    }

    @Test
    @Order(3)
    void testIpBasedContractGetFunction() {
        IpBasedContract savedContract = ipBasedContractRepository.save(testContract);
        IpBasedContract retrievedContract = ipBasedContractRepository.findById(savedContract.getId())
                .orElseThrow(() -> new ContractNotFoundException(savedContract.getId()));

        assertEquals(savedContract.getId(), retrievedContract.getId());
        assertEquals("Test IP Contract", retrievedContract.getDescription());
        assertEquals(testExecutor.getId(), retrievedContract.getExecutor().getId());
        assertEquals(testIp.getId(), retrievedContract.getIntellectualProperty().getId());
    }

    @Test
    @Order(4)
    void testFindIpBasedContractsWhereStakeholderIsExecutor() {
        // Save initial contract
        IpBasedContract savedContract1 = ipBasedContractRepository.save(testContract);

        // Create and save another contract with same executor
        IpBasedContract contract2 = new IpBasedContract(
                "Test IP Contract 2",
                LocalDate.now(),
                2,
                testIp,
                testExecutor
        );
        IpBasedContract savedContract2 = ipBasedContractRepository.save(contract2);

        // Create another stakeholder and contract
        StakeHolder otherExecutor = new StakeHolder("Other Executor", testRole);
        otherExecutor = stakeHolderRepository.save(otherExecutor);

        IpBasedContract contract3 = new IpBasedContract(
                "Test IP Contract 3",
                LocalDate.now(),
                3,
                testIp,
                otherExecutor
        );
        ipBasedContractRepository.save(contract3);

        // Test the custom finder method
        Set<IpBasedContract> executorContracts = ipBasedContractRepository
                .findIpBasedContractsWhereStakeholderIsExecutor(testExecutor.getId());

        assertEquals(2, executorContracts.size());
        assertTrue(executorContracts.stream()
                .map(IpBasedContract::getId)
                .allMatch(id ->
                        id.equals(savedContract1.getId()) ||
                                id.equals(savedContract2.getId())
                ));
    }

    @Test
    @Order(5)
    void testGetIpBasedContractByIpId() {
        // Save initial contract
        IpBasedContract savedContract1 = ipBasedContractRepository.save(testContract);

        // Create another IP and contract
        IntellectualProperty otherIp = new IntellectualProperty("Other IP", "Other IP Description");
        otherIp = intellectualPropertyRepository.save(otherIp);

        IpBasedContract contract2 = new IpBasedContract(
                "Test IP Contract 2",
                LocalDate.now(),
                2,
                otherIp,
                testExecutor
        );
        ipBasedContractRepository.save(contract2);

        // Test the custom finder method
        Set<IpBasedContract> ipContracts = ipBasedContractRepository
                .getIpBasedContractByIpId(testIp.getId());

        assertEquals(1, ipContracts.size());
        assertEquals(savedContract1.getId(), ipContracts.iterator().next().getId());
    }

    @Test
    @Order(6)
    void testFindByIds() {
        // Save multiple contracts
        IpBasedContract savedContract1 = ipBasedContractRepository.save(testContract);

        IpBasedContract contract2 = new IpBasedContract(
                "Test IP Contract 2",
                LocalDate.now(),
                2,
                testIp,
                testExecutor
        );
        IpBasedContract savedContract2 = ipBasedContractRepository.save(contract2);

        IpBasedContract contract3 = new IpBasedContract(
                "Test IP Contract 3",
                LocalDate.now(),
                3,
                testIp,
                testExecutor
        );
        IpBasedContract savedContract3 = ipBasedContractRepository.save(contract3);

        // Test finding subset of contracts
        List<IpBasedContract> foundContracts = ipBasedContractRepository.findByIds(
                Arrays.asList(savedContract1.getId(), savedContract3.getId())
        );

        assertEquals(2, foundContracts.size());
        assertTrue(foundContracts.stream()
                .map(IpBasedContract::getId)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList(savedContract1.getId(), savedContract3.getId()))
        );
        assertTrue(foundContracts.stream()
                .map(IpBasedContract::getDescription)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList("Test IP Contract", "Test IP Contract 3"))
        );

        // Test with empty list
        List<IpBasedContract> emptyResult = ipBasedContractRepository.findByIds(Collections.emptyList());
        assertTrue(emptyResult.isEmpty());

        // Test with non-existent IDs
        List<IpBasedContract> nonExistentResults = ipBasedContractRepository.findByIds(
                Arrays.asList(999L, 888L)
        );
        assertTrue(nonExistentResults.isEmpty());
    }
}
