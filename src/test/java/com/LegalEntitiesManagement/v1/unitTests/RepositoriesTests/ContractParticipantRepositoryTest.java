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
}
