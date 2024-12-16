package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractParticipantRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ContractParticipantRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private ContractParticipantRepository contractParticipantRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    private Role testRole;
    private StakeHolder testExecutor;
    private StakeHolder testParticipant;
    private Contract testContract;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);

        // Create test participant
        testParticipant = new StakeHolder("Test Participant", testRole);
        testParticipant = stakeHolderRepository.save(testParticipant);

        // Create test contract
        testContract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        testContract = contractRepository.save(testContract);
    }

    @Test
    @Order(1)
    void testSaveContractParticipant() {
        ContractParticipant participant = new ContractParticipant(
                testContract,
                50.0,
                true,
                testExecutor
        );

        ContractParticipant savedParticipant = contractParticipantRepository.save(participant);

        assertNotNull(savedParticipant.getId());
        assertEquals(testContract.getId(), savedParticipant.getContract().getId());
        assertEquals(testExecutor.getId(), savedParticipant.getStakeholder().getId());
        assertEquals(50.0, savedParticipant.getPercentage());
        assertTrue(savedParticipant.getIsExecutor());
    }

    @Test
    @Order(2)
    void testFindParticipantsByContractId() {
        // Create and save two participants
        ContractParticipant executor = new ContractParticipant(
                testContract,
                60.0,
                true,
                testExecutor
        );
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );

        contractParticipantRepository.save(executor);
        contractParticipantRepository.save(participant);

        Set<ContractParticipant> participants = contractParticipantRepository
                .findParticipantsByContractId(testContract.getId());

        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertTrue(participants.stream()
                .anyMatch(p -> p.getStakeholder().getId().equals(testExecutor.getId())));
        assertTrue(participants.stream()
                .anyMatch(p -> p.getStakeholder().getId().equals(testParticipant.getId())));
    }

    @Test
    @Order(3)
    void testFindExecutorByContractId() {
        // Create and save executor participant
        ContractParticipant executor = new ContractParticipant(
                testContract,
                60.0,
                true,
                testExecutor
        );
        contractParticipantRepository.save(executor);

        Optional<ContractParticipant> foundExecutor = contractParticipantRepository
                .findExecutorByContractId(testContract.getId());

        assertTrue(foundExecutor.isPresent());
        assertTrue(foundExecutor.get().getIsExecutor());
        assertEquals(testExecutor.getId(), foundExecutor.get().getStakeholder().getId());
    }

    @Test
    @Order(4)
    void testIsStakeholderParticipantInContract() {
        // Create and save a participant
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );
        contractParticipantRepository.save(participant);

        boolean isParticipant = contractParticipantRepository
                .isStakeholderParticipantInContract(testContract.getId(), testParticipant.getId());
        boolean isNonParticipant = contractParticipantRepository
                .isStakeholderParticipantInContract(testContract.getId(), 999L);

        assertTrue(isParticipant);
        assertFalse(isNonParticipant);
    }

    @Test
    @Order(5)
    void testFindById() {
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );
        ContractParticipant savedParticipant = contractParticipantRepository.save(participant);

        Optional<ContractParticipant> found = contractParticipantRepository
                .findById(savedParticipant.getId());

        assertTrue(found.isPresent());
        assertEquals(savedParticipant.getId(), found.get().getId());
        assertEquals(testParticipant.getId(), found.get().getStakeholder().getId());
        assertEquals(testContract.getId(), found.get().getContract().getId());
    }

    @Test
    @Order(6)
    void testExistsById() {
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );
        ContractParticipant savedParticipant = contractParticipantRepository.save(participant);

        boolean exists = contractParticipantRepository.existsById(savedParticipant.getId());
        boolean notExists = contractParticipantRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @Order(7)
    void testFindAll() {
        // Create and save two participants
        ContractParticipant executor = new ContractParticipant(
                testContract,
                60.0,
                true,
                testExecutor
        );
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );

        contractParticipantRepository.save(executor);
        contractParticipantRepository.save(participant);

        var allParticipants = contractParticipantRepository.findAll();

        assertNotNull(allParticipants);
        assertTrue(allParticipants.size() >= 2);
        assertTrue(allParticipants.stream()
                .anyMatch(p -> p.getStakeholder().getId().equals(testExecutor.getId())));
        assertTrue(allParticipants.stream()
                .anyMatch(p -> p.getStakeholder().getId().equals(testParticipant.getId())));
    }

    @Test
    @Order(8)
    void testFindByStakeholderId() {
        // Create and save multiple contracts and participants to test comprehensive retrieval
        Contract testContract2 = new Contract(
                "Test Contract 2",
                LocalDate.now(),
                2,
                testExecutor
        );
        testContract2 = contractRepository.save(testContract2);

        // Create participants in first contract
        ContractParticipant participant1 = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );
        ContractParticipant participant2 = new ContractParticipant(
                testContract2,
                30.0,
                false,
                testParticipant
        );

        // Save participants
        contractParticipantRepository.save(participant1);
        contractParticipantRepository.save(participant2);

        // Test finding by stakeholder ID
        Set<ContractParticipant> foundParticipants = contractParticipantRepository
                .findByStakeholderId(testParticipant.getId());

        // Verify results
        assertNotNull(foundParticipants);
        assertEquals(2, foundParticipants.size());
        assertTrue(foundParticipants.stream()
                .allMatch(p -> p.getStakeholder().getId().equals(testParticipant.getId())));
        assertTrue(foundParticipants.stream()
                .map(ContractParticipant::getContract)
                .map(Contract::getId)
                .collect(Collectors.toSet())
                .containsAll(Set.of(testContract.getId(), testContract2.getId())));
    }

    @Test
    @Order(9)
    void testFindParticipantsByContractIds() {
        // Create a second test contract
        Contract testContract2 = new Contract(
                "Test Contract 2",
                LocalDate.now(),
                2,
                testExecutor
        );
        testContract2 = contractRepository.save(testContract2);

        // Create participants for both contracts
        ContractParticipant participant1 = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );
        ContractParticipant participant2 = new ContractParticipant(
                testContract2,
                30.0,
                false,
                testParticipant
        );
        ContractParticipant executor1 = new ContractParticipant(
                testContract,
                60.0,
                true,
                testExecutor
        );
        ContractParticipant executor2 = new ContractParticipant(
                testContract2,
                70.0,
                true,
                testExecutor
        );

        // Save all participants
        contractParticipantRepository.save(participant1);
        contractParticipantRepository.save(participant2);
        contractParticipantRepository.save(executor1);
        contractParticipantRepository.save(executor2);

        // Test finding participants by multiple contract IDs
        Set<ContractParticipant> foundParticipants = contractParticipantRepository
                .findParticipantsByContractIds(Set.of(testContract.getId(), testContract2.getId()));

        // Verify results
        assertNotNull(foundParticipants);
        assertEquals(4, foundParticipants.size());

        // Check if we have participants from both contracts
        Set<Long> foundContractIds = foundParticipants.stream()
                .map(p -> p.getContract().getId())
                .collect(Collectors.toSet());
        assertEquals(Set.of(testContract.getId(), testContract2.getId()), foundContractIds);

        // Check if we have both stakeholders in results
        Set<Long> foundStakeholderIds = foundParticipants.stream()
                .map(p -> p.getStakeholder().getId())
                .collect(Collectors.toSet());
        assertEquals(Set.of(testExecutor.getId(), testParticipant.getId()), foundStakeholderIds);

        // Check if we have correct number of executors and non-executors
        long executorCount = foundParticipants.stream()
                .filter(ContractParticipant::getIsExecutor)
                .count();
        assertEquals(2, executorCount);
    }

    @Test
    @Order(10)
    void testDeleteAllByContractId() {
        // Create multiple participants for the test contract
        ContractParticipant executor = new ContractParticipant(
                testContract,
                60.0,
                true,
                testExecutor
        );
        ContractParticipant participant = new ContractParticipant(
                testContract,
                40.0,
                false,
                testParticipant
        );

        // Save the participants
        contractParticipantRepository.save(executor);
        contractParticipantRepository.save(participant);

        // Verify participants exist before deletion
        Set<ContractParticipant> beforeDeletion = contractParticipantRepository
                .findParticipantsByContractId(testContract.getId());
        assertEquals(2, beforeDeletion.size());

        // Perform the deletion
        contractParticipantRepository.deleteAllByContractId(testContract.getId());

        // Verify all participants for the contract were deleted
        Set<ContractParticipant> afterDeletion = contractParticipantRepository
                .findParticipantsByContractId(testContract.getId());
        assertTrue(afterDeletion.isEmpty());

        // Test deleting from a contract with no participants (should not throw exception)
        Contract emptyContract = new Contract(
                "Empty Contract",
                LocalDate.now(),
                3,
                testExecutor
        );
        emptyContract = contractRepository.save(emptyContract);
        contractParticipantRepository.deleteAllByContractId(emptyContract.getId());
    }
}
