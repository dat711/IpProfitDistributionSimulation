package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
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
public class ContractRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    private Role testRole;
    private StakeHolder testExecutor;
    private Contract testContract;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);

        // Create test contract
        testContract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
    }

    @Test
    @Order(1)
    void testContractInsertionFunction() {
        Contract savedContract = contractRepository.save(testContract);
        assertNotNull(savedContract.getId());
        assertEquals("Test Contract", savedContract.getDescription());
        assertEquals(testExecutor.getId(), savedContract.getExecutor().getId());
    }

    @Test
    @Order(2)
    void testContractExistFunction() {
        Contract savedContract = contractRepository.save(testContract);
        assertTrue(contractRepository.existsById(savedContract.getId()));
    }

    @Test
    @Order(3)
    void testContractGetFunction() {
        Contract savedContract = contractRepository.save(testContract);
        Contract retrievedContract = contractRepository.findById(savedContract.getId())
                .orElseThrow(() -> new ContractNotFoundException(savedContract.getId()));

        assertEquals(savedContract.getId(), retrievedContract.getId());
        assertEquals("Test Contract", retrievedContract.getDescription());
        assertEquals(testExecutor.getId(), retrievedContract.getExecutor().getId());
    }

    @Test
    @Order(4)
    void testFindContractsWhereStakeholderIsExecutor() {
        // Save initial contract
        Contract savedContract1 = contractRepository.save(testContract);

        // Create and save another contract with same executor
        Contract contract2 = new Contract(
                "Test Contract 2",
                LocalDate.now(),
                2,
                testExecutor
        );
        Contract savedContract2 = contractRepository.save(contract2);

        // Create another stakeholder and contract
        StakeHolder otherExecutor = new StakeHolder("Other Executor", testRole);
        otherExecutor = stakeHolderRepository.save(otherExecutor);

        Contract contract3 = new Contract(
                "Test Contract 3",
                LocalDate.now(),
                3,
                otherExecutor
        );
        contractRepository.save(contract3);

        // Test the custom finder method
        Set<Contract> executorContracts = contractRepository
                .findContractsWhereStakeholderIsExecutor(testExecutor.getId());

        assertEquals(2, executorContracts.size());
        assertTrue(executorContracts.stream()
                .map(Contract::getId)
                .allMatch(id ->
                        id.equals(savedContract1.getId()) ||
                                id.equals(savedContract2.getId())
                ));
    }

    @Test
    @Order(5)
    void testFindByIds() {
        // Save multiple contracts
        Contract savedContract1 = contractRepository.save(testContract);

        Contract contract2 = new Contract(
                "Test Contract 2",
                LocalDate.now(),
                2,
                testExecutor
        );
        Contract savedContract2 = contractRepository.save(contract2);

        Contract contract3 = new Contract(
                "Test Contract 3",
                LocalDate.now(),
                3,
                testExecutor
        );
        Contract savedContract3 = contractRepository.save(contract3);

        // Test finding subset of contracts
        List<Contract> foundContracts = contractRepository.findByIds(
                Arrays.asList(savedContract1.getId(), savedContract3.getId())
        );

        assertEquals(2, foundContracts.size());
        assertTrue(foundContracts.stream()
                .map(Contract::getId)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList(savedContract1.getId(), savedContract3.getId()))
        );
        assertTrue(foundContracts.stream()
                .map(Contract::getDescription)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList("Test Contract", "Test Contract 3"))
        );

        // Test with empty list
        List<Contract> emptyResult = contractRepository.findByIds(Collections.emptyList());
        assertTrue(emptyResult.isEmpty());

        // Test with non-existent IDs
        List<Contract> nonExistentResults = contractRepository.findByIds(
                Arrays.asList(999L, 888L)
        );
        assertTrue(nonExistentResults.isEmpty());
    }
}
