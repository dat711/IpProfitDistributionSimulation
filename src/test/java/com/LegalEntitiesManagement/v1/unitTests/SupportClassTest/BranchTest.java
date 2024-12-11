package com.LegalEntitiesManagement.v1.unitTests.SupportClassTest;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.Branch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class BranchTest {
    private Role executorRole;
    private Role participantRole;
    private StakeHolder executor;
    private StakeHolder participant1;
    private StakeHolder participant2;
    private IpBasedContract contract1;
    private IpBasedContract contract2;
    private IntellectualProperty ip;

    @BeforeEach
    void setUp() {
        // Set up roles
        executorRole = new Role("Executor", "Main executor role", 100);
        executorRole.setId(1L);
        participantRole = new Role("Participant", "Regular participant", 50);
        participantRole.setId(2L);

        // Set up stakeholders
        executor = new StakeHolder("Main Executor", executorRole);
        executor.setId(1L);
        participant1 = new StakeHolder("Participant 1", participantRole);
        participant1.setId(2L);
        participant2 = new StakeHolder("Participant 2", participantRole);
        participant2.setId(3L);

        // Set up IP
        ip = new IntellectualProperty("Test IP", "Test IP Description");
        ip.setId(1L);

        // Set up contracts
        contract1 = new IpBasedContract("Contract 1", LocalDate.now(), 1, ip, executor);
        contract1.setId(1L);
        contract2 = new IpBasedContract("Contract 2", LocalDate.now(), 2, ip, executor);
        contract2.setId(2L);

        // Set up contract participants
        Set<ContractParticipant> participants1 = new HashSet<>();
        participants1.add(new ContractParticipant(contract1, 60.0, true, executor));
        participants1.add(new ContractParticipant(contract1, 40.0, false, participant1));
        contract1.setContractParticipants(participants1);

        Set<ContractParticipant> participants2 = new HashSet<>();
        participants2.add(new ContractParticipant(contract2, 70.0, true, executor));
        participants2.add(new ContractParticipant(contract2, 30.0, false, participant2));
        contract2.setContractParticipants(participants2);
    }

    @Test
    @DisplayName("Should create branch with single contract")
    void createBranchSingleContract() {
        Set<IpBasedContract> contracts = new HashSet<>(Collections.singletonList(contract1));
        Branch branch = new Branch(contracts);

        assertEquals(1, branch.getContracts().size());
        assertEquals(executor, branch.getExecutor());
        assertEquals(1, branch.getNonExecutorSet().size());
        assertTrue(branch.getNonExecutorSet().contains(participant1));
    }

    @Test
    @DisplayName("Should create branch with multiple contracts")
    void createBranchMultipleContracts() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(contract1, contract2));
        Branch branch = new Branch(contracts);

        assertEquals(2, branch.getContracts().size());
        assertEquals(executor, branch.getExecutor());
        assertEquals(2, branch.getNonExecutorSet().size());
        assertTrue(branch.getNonExecutorSet().contains(participant1));
        assertTrue(branch.getNonExecutorSet().contains(participant2));
    }

    @Test
    @DisplayName("Should sort contracts by priority in descending order")
    void sortContractsByPriority() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(contract1, contract2));
        Branch branch = new Branch(contracts);

        List<IpBasedContract> sortedContracts = branch.getSortedContract();
        assertEquals(2, sortedContracts.size());
        assertEquals(contract2.getId(), sortedContracts.get(0).getId()); // Priority 2
        assertEquals(contract1.getId(), sortedContracts.get(1).getId()); // Priority 1
    }

    @Test
    @DisplayName("Should merge branches with same executor")
    void mergeBranchesWithSameExecutor() {
        // Create first branch
        Set<IpBasedContract> contracts1 = new HashSet<>(Collections.singletonList(contract1));
        Branch branch1 = new Branch(contracts1);

        // Create second branch
        Set<IpBasedContract> contracts2 = new HashSet<>(Collections.singletonList(contract2));
        Branch branch2 = new Branch(contracts2);

        // Merge branches
        branch1.addBranch(branch2);

        assertEquals(2, branch1.getContracts().size());
        assertEquals(2, branch1.getNonExecutorSet().size());
        assertEquals(executor, branch1.getExecutor());
    }

    @Test
    @DisplayName("Should throw exception when merging branches with different executors")
    void mergeBranchesWithDifferentExecutors() {
        // Create first branch
        Set<IpBasedContract> contracts1 = new HashSet<>(Collections.singletonList(contract1));
        Branch branch1 = new Branch(contracts1);

        // Create second contract with different executor
        StakeHolder differentExecutor = new StakeHolder("Different Executor", executorRole);
        differentExecutor.setId(4L);
        IpBasedContract contract3 = new IpBasedContract("Contract 3", LocalDate.now(), 3, ip, differentExecutor);
        contract3.setId(3L);

        Set<ContractParticipant> participants3 = new HashSet<>();
        participants3.add(new ContractParticipant(contract3, 80.0, true, differentExecutor));
        participants3.add(new ContractParticipant(contract3, 20.0, false, participant1));
        contract3.setContractParticipants(participants3);

        Set<IpBasedContract> contracts2 = new HashSet<>(Collections.singletonList(contract3));
        Branch branch2 = new Branch(contracts2);

        assertThrows(ContractValidationFailed.class, () -> branch1.addBranch(branch2));
    }

    @Test
    @DisplayName("Should check if branch has specific stakeholder")
    void checkHasStakeholder() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(contract1, contract2));
        Branch branch = new Branch(contracts);

        assertTrue(branch.haveThisStakeHolder(participant1));
        assertTrue(branch.haveThisStakeHolder(participant2));
        assertFalse(branch.haveThisStakeHolder(new StakeHolder("Unknown", participantRole)));
    }

    @Test
    @DisplayName("Should compare stakeholder roles with executor")
    void compareStakeholderRolesWithExecutor() {
        Set<IpBasedContract> contracts = new HashSet<>(Collections.singletonList(contract1));
        Branch branch = new Branch(contracts);

        // Test with equal priority
        StakeHolder equalPriorityStakeholder = new StakeHolder("Equal", executorRole);
        assertTrue(branch.isHigherEqualExecutor(equalPriorityStakeholder));

        // Test with higher priority
        Role higherRole = new Role("Higher", "Higher priority role", 150);
        StakeHolder higherPriorityStakeholder = new StakeHolder("Higher", higherRole);
        assertTrue(branch.isHigherEqualExecutor(higherPriorityStakeholder));

        // Test with lower priority
        Role lowerRole = new Role("Lower", "Lower priority role", 50);
        StakeHolder lowerPriorityStakeholder = new StakeHolder("Lower", lowerRole);
        assertFalse(branch.isHigherEqualExecutor(lowerPriorityStakeholder));
    }

    @Test
    @DisplayName("Should get examined stakeholders")
    void getExaminedStakeholders() {
        Set<IpBasedContract> contracts = new HashSet<>(Arrays.asList(contract1, contract2));
        Branch branch = new Branch(contracts);

        Set<StakeHolder> stakeholdersToExamine = new HashSet<>(Arrays.asList(participant1, participant2, executor));
        Set<StakeHolder> examined = branch.getExamined(stakeholdersToExamine);

        assertEquals(2, examined.size());
        assertTrue(examined.contains(participant1));
        assertTrue(examined.contains(participant2));
        assertFalse(examined.contains(executor)); // Executor should be excluded
    }

    @Test
    @DisplayName("Should correctly check if branch has target")
    void checkHasTarget() {
        // Create first branch with participant2 as non-executor
        Set<ContractParticipant> participants1 = new HashSet<>();
        participants1.add(new ContractParticipant(contract1, 60.0, true, executor));
        participants1.add(new ContractParticipant(contract1, 40.0, false, participant2));
        contract1.setContractParticipants(participants1);
        Set<IpBasedContract> contracts1 = new HashSet<>(Collections.singletonList(contract1));
        Branch branch1 = new Branch(contracts1);

        // Create second branch where participant2 is the executor
        IpBasedContract contract3 = new IpBasedContract("Contract 3", LocalDate.now(), 3, ip, participant2);
        Set<ContractParticipant> participants3 = new HashSet<>();
        participants3.add(new ContractParticipant(contract3, 70.0, true, participant2));
        participants3.add(new ContractParticipant(contract3, 30.0, false, participant1));
        contract3.setContractParticipants(participants3);
        Set<IpBasedContract> contracts2 = new HashSet<>(Collections.singletonList(contract3));
        Branch branch2 = new Branch(contracts2);

        // branch1 has participant2 (branch2's executor) in its non-executor set
        assertTrue(branch1.haveTarget(branch2));

        // branch2 doesn't have executor of branch1 in its non-executor set
        assertFalse(branch2.haveTarget(branch1));

        // Create a third branch with completely different stakeholders
        StakeHolder differentExecutor = new StakeHolder("Different Executor", executorRole);
        StakeHolder differentParticipant = new StakeHolder("Different Participant", participantRole);
        IpBasedContract contract4 = new IpBasedContract("Contract 4", LocalDate.now(), 4, ip, differentExecutor);
        Set<ContractParticipant> participants4 = new HashSet<>();
        participants4.add(new ContractParticipant(contract4, 80.0, true, differentExecutor));
        participants4.add(new ContractParticipant(contract4, 20.0, false, differentParticipant));
        contract4.setContractParticipants(participants4);
        Set<IpBasedContract> contracts3 = new HashSet<>(Collections.singletonList(contract4));
        Branch branch3 = new Branch(contracts3);

        // Neither branch has the other's executor in their non-executor sets
        assertFalse(branch1.haveTarget(branch3));
        assertFalse(branch3.haveTarget(branch1));
    }
}
