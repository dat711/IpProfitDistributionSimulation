package com.LegalEntitiesManagement.v1.unitTests.SupportClassTest;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.*;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyNodeBranchBuilderTest {
    private Role participantRole;

    private IntellectualProperty ip;

    private Counter moneyNodeCounter, stakeHolderCounter, ipBasedContractCounter, participantCounter;

    private StakeHolder topExecutor;

    private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

    private ContractNode contractNodeProvider(ContractNode contractNode){
        contractNode.setId(moneyNodeCounter.getId());
        return contractNode;
    }

    private boolean validResponsibilitySourceType(MoneyNodeBranch examinedBranch){
        return examinedBranch.getResponsibilities().stream().noneMatch(
                responsibility -> responsibility.getSource() instanceof StakeHolderLeaf
        );
    }

    @BeforeEach
    void setUp(){
        // Set up roles
        Role topExecutorRole = new Role("Top Executor", "Mock top executor", 6);
        topExecutorRole.setId(1L);

        participantRole = new Role("Participant role", "Mock participant", 1);
        participantRole.setId(3L);

        // Set up intellectualProperty
        ip = new IntellectualProperty();
        ip.setName("Test Ip");
        ip.setId(1L);

        // Set up new counter
        moneyNodeCounter = new Counter();
        stakeHolderCounter = new Counter();
        ipBasedContractCounter = new Counter();
        participantCounter = new Counter();


        // Set up new StakeHolder
        topExecutor = TestDataFactory.genStakeHolder(topExecutorRole, stakeHolderCounter);

        // set up leaves
        mapLeaves = TestDataFactory.genMapLeaves(moneyNodeCounter, topExecutor);
    }

    @Nested
    @DisplayName("Test MoneyNodeBranch Builder")
    class builderTest{
        @Test
        @DisplayName("Should create MoneyNodeBranch with single contract")
        void createMoneyNodeBranchWithSingleContract() {
            // Arrange
            List<StakeHolder> participants = Arrays.asList(topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter));

            IpBasedContract contract = TestDataFactory.genIpBasedContract(
                    topExecutor, participants, ip, ipBasedContractCounter, participantCounter, 1);

            Branch branch = new Branch(Collections.singleton(contract));

            // Act
            MoneyNodeBranch moneyNodeBranch = new MoneyNodeBranch.Builder(branch)
                    .addNewContractNodeBuilder(MoneyNodeBranchBuilderTest.this::contractNodeProvider)
                    .getLeaves(mapLeaves)
                    .build();

            // Assert
            assertNotNull(moneyNodeBranch);
            assertTrue(MoneyNodeBranchBuilderTest.this.validResponsibilitySourceType(moneyNodeBranch));
            assertEquals(1, moneyNodeBranch.getContractNodes().size());
            assertEquals(2, moneyNodeBranch.getResponsibilities().size()); // One for each participant
            assertEquals(branch.getExecutor(), moneyNodeBranch.getExecutorLeaf().getStakeHolder());
        }


        @Test
        @DisplayName("Should create MoneyNodeBranch with multiple contracts in sequence")
        void createMoneyNodeBranchWithMultipleContracts() {
            // Arrange
            List<StakeHolder> participants = TestDataFactory.genStakeHolders(participantRole, 3, stakeHolderCounter);
            List<List<StakeHolder>> contractParticipants = participants.stream()
                    .map(participant -> Arrays.asList(topExecutor, participant))
                    .toList();

            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter, participants.stream().filter(
                    participant -> !participant.equals(topExecutor)).toArray(StakeHolder[]::new)));

            if (participants.get(0).equals(participants.get(1))){
                System.out.println("Overlap bug");
            }

            List<IpBasedContract> contracts = TestDataFactory.genBranchContracts(
                    topExecutor, contractParticipants, ip, ipBasedContractCounter, participantCounter);

            Branch branch = new Branch(contracts);

            // Act
            MoneyNodeBranch moneyNodeBranch = new MoneyNodeBranch.Builder(branch)
                    .addNewContractNodeBuilder(MoneyNodeBranchBuilderTest.this::contractNodeProvider)
                    .getLeaves(mapLeaves)
                    .build();

            // Assert
            assertNotNull(moneyNodeBranch);
            assertEquals(3, moneyNodeBranch.getContractNodes().size());
            assertFalse(moneyNodeBranch.getContractNodes().isEmpty());

            // Verify branch root is highest priority contract
            ContractNode rootNode = moneyNodeBranch.getBranchRoot();
            assertEquals(3, rootNode.getContract().getContractPriority());

            // Verify responsibilities are properly connected
            Collection<Responsibility> responsibilities = moneyNodeBranch.getResponsibilities();
            assertFalse(responsibilities.isEmpty());

            // Verify each node except last points to next node instead of executor leaf
            List<ContractNode> sortedNodes = new ArrayList<>(moneyNodeBranch.getContractNodes());
            sortedNodes.sort((a, b) -> Integer.compare(
                    b.getContract().getContractPriority(),
                    a.getContract().getContractPriority()
            ));

            for (int i = 0; i < sortedNodes.size() - 1; i++) {
                ContractNode currentNode = sortedNodes.get(i);
                ContractNode nextNode = sortedNodes.get(i + 1);

                boolean hasPathToNext = currentNode.getDownStreamEdges().stream()
                        .anyMatch(r -> r.getTarget().equals(nextNode));
                assertTrue(hasPathToNext, "Node should connect to next node in sequence");
            }

            // Verify last node connects to executor leaf
            ContractNode lastNode = sortedNodes.get(sortedNodes.size() - 1);
            boolean connectsToExecutor = lastNode.getDownStreamEdges().stream()
                    .anyMatch(r -> r.getTarget().equals(moneyNodeBranch.getExecutorLeaf()));
            assertTrue(connectsToExecutor, "Last node should connect to executor leaf");

            assertTrue(MoneyNodeBranchBuilderTest.this.validResponsibilitySourceType(moneyNodeBranch));

        }

        @Test
        @DisplayName("Should properly map switch targets")
        void verifySwitchTargetsMapping() {
            // Arrange
            List<StakeHolder> participants = TestDataFactory.genStakeHolders(participantRole, 2, stakeHolderCounter);
            participants.add(topExecutor);

            IpBasedContract contract = TestDataFactory.genIpBasedContract(
                    topExecutor, participants, ip, ipBasedContractCounter, participantCounter, 1);

            Branch branch = new Branch(Collections.singleton(contract));

            // Act
            MoneyNodeBranch moneyNodeBranch = new MoneyNodeBranch.Builder(branch)
                    .addNewContractNodeBuilder(MoneyNodeBranchBuilderTest.this::contractNodeProvider)
                    .getLeaves(mapLeaves)
                    .build();

            // Assert
            Map<StakeHolderLeaf, Responsibility> switchTargets = moneyNodeBranch.getSwitchTarget();
            assertNotNull(switchTargets);
            assertFalse(switchTargets.isEmpty());

            // Verify each participant has a corresponding switch target
            for (StakeHolder participant : participants) {
                StakeHolderLeaf leaf = mapLeaves.get(participant);
                if (leaf != null) {
                    assertTrue(switchTargets.containsKey(leaf),
                            "Should have switch target for participant: " + participant.getName());
                }
            }
        }
    }

    @Nested
    @DisplayName("Test MoneyNodeBranch Builder State Requirements")
    class BuilderStateTest {
        private Branch validBranch;
        private List<StakeHolder> participants;

        @BeforeEach
        void setUpBranch() {
            participants = Arrays.asList(topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter));

            IpBasedContract contract = TestDataFactory.genIpBasedContract(
                    topExecutor, participants, ip, ipBasedContractCounter, participantCounter, 1);

            validBranch = new Branch(Collections.singleton(contract));
        }

        @Test
        @DisplayName("Should throw exception when building without ContractNodeBuilder")
        void shouldThrowExceptionWithoutContractNodeBuilder() {
            // Arrange & Act
            MoneyNodeBranch.Builder builder = new MoneyNodeBranch.Builder(validBranch)
                    .getLeaves(mapLeaves);

            // Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("contractNode Supplier"));
        }

        @Test
        @DisplayName("Should throw exception when building without leaves mapping")
        void shouldThrowExceptionWithoutLeaves() {
            // Arrange & Act
            MoneyNodeBranch.Builder builder = new MoneyNodeBranch.Builder(validBranch)
                    .addNewContractNodeBuilder(MoneyNodeBranchBuilderTest.this::contractNodeProvider);

            // Assert
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    builder::build
            );
            assertTrue(exception.getMessage().contains("leaves must be filled"));
        }

        @Test
        @DisplayName("Should build successfully when all required components are provided")
        void shouldBuildSuccessfullyWithAllComponents() {
            // Arrange
            MoneyNodeBranch.Builder builder = new MoneyNodeBranch.Builder(validBranch)
                    .addNewContractNodeBuilder(MoneyNodeBranchBuilderTest.this::contractNodeProvider)
                    .getLeaves(mapLeaves);

            // Act & Assert
            assertDoesNotThrow(builder::build);
        }

    }
}
