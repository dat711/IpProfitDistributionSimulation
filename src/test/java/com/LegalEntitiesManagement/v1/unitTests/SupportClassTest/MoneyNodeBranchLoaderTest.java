package com.LegalEntitiesManagement.v1.unitTests.SupportClassTest;

import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.Branch;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.MoneyNodeBranch;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.stream.Collectors;

import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoneyNodeBranchLoaderTest {
    private Role participantRole;

    private IntellectualProperty ip;

    private Counter moneyNodeCounter, stakeHolderCounter, ipBasedContractCounter, participantCounter, responsibilityCounter;

    private StakeHolder topExecutor;

    private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

    private Map<IpBasedContract, ContractNode> contractNodeMap;

    private Map<Long, Collection<Responsibility>> mapResponsibilitySourceKey, mapResponsibilityTargetKey;

    private Set<Responsibility> allResponsibilities;

    private ContractNode contractNodeProvider(ContractNode contractNode){
        contractNode.setId(moneyNodeCounter.getId());
        return contractNode;
    }

    private Collection<ContractNode> contractNodeLoader(Collection<IpBasedContract> contracts){
        return contractNodeMap.entrySet().stream().filter(
                entry -> contracts.contains(entry.getKey())
        ).map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    private Map<Long,Collection<Responsibility>> upperNodeLoader(Collection<Long> targetIds){
        return mapResponsibilityTargetKey.entrySet().stream().filter(
                entry -> targetIds.contains(entry.getKey())
        ).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue
        ));
    }

    private Optional<Responsibility> tailResponsibilityLoader(Long sourceID, Long targetID){
        return  mapResponsibilitySourceKey.get(sourceID).stream().filter(
                responsibility -> responsibility.getTarget().getId().equals(targetID)
        ).findFirst();
    }


    @BeforeEach
    void setUp(){
        // Set up roles
        Role topExecutorRole = new Role("Top Executor", "Mock top executor", 6);
        topExecutorRole.setId(1L);

        participantRole = new Role("Participant role", "Mock participant", 1);
        participantRole.setId(2L);

        // Set up intellectualProperty
        ip = new IntellectualProperty();
        ip.setName("Test Ip");
        ip.setId(1L);

        // Set up new counter
        moneyNodeCounter = new Counter();
        stakeHolderCounter = new Counter();
        ipBasedContractCounter = new Counter();
        participantCounter = new Counter();
        responsibilityCounter = new Counter();

        // Set up new StakeHolder
        topExecutor = TestDataFactory.genStakeHolder(topExecutorRole, stakeHolderCounter);

        // set up leaves
        mapLeaves = TestDataFactory.genMapLeaves(moneyNodeCounter, topExecutor);
    }

    private void populateResponsibilityMap(){

        mapResponsibilitySourceKey = allResponsibilities.stream().collect(Collectors.groupingBy(
                responsibility -> responsibility.getSource().getId()
        )).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> new HashSet<>(entry.getValue())
        ));

        mapResponsibilityTargetKey = allResponsibilities.stream().collect(Collectors.groupingBy(
                responsibility -> responsibility.getTarget().getId()
        )).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> new HashSet<>(entry.getValue())
        ));
    }

    @Nested
    @DisplayName("Single Contract Merge Tests")
    class SingleContractMergeTests {
        @Test
        @DisplayName("Should merge single contract as new root")
        void mergeSingleContractAsNewRoot() {
            // Arrange
            List<StakeHolder> originalParticipants = Arrays.asList(
                    topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)
            );

            List<StakeHolder> newParticipants = Arrays.asList(
                    topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)
            );

            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,
                    originalParticipants.stream()
                            .filter(p -> !p.equals(topExecutor))
                            .toArray(StakeHolder[]::new)
            ));

            // Create original contract with lower priority
            IpBasedContract originalContract = TestDataFactory.genIpBasedContract(
                    topExecutor, originalParticipants, ip, ipBasedContractCounter, participantCounter, 4
            );

            // Create new contract with higher priority
            IpBasedContract newContract = TestDataFactory.genIpBasedContract(
                    topExecutor, newParticipants, ip, ipBasedContractCounter, participantCounter, 6
            );

            // Set up contract nodes and responsibilities
            List<ContractNode> allNodes = TestDataFactory.genContractNodes(
                    Collections.singleton(originalContract),
                    moneyNodeCounter,
                    responsibilityCounter,
                    mapLeaves.get(topExecutor)
            );

            contractNodeMap = allNodes.stream().collect(Collectors.toMap(
                    node -> (IpBasedContract) node.getContract(),
                    node -> node
            ));

            allResponsibilities = allNodes.stream()
                    .map(ContractNode::getDownStreamEdges)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            populateResponsibilityMap();

            // Create and merge branches
            Branch originalBranch = new Branch(Collections.singleton(originalContract));
            Branch newBranch = new Branch(Collections.singleton(newContract));

            // Act
            MoneyNodeBranch mergedBranch = new MoneyNodeBranch.Loader(originalBranch)
                    .addBranch(newBranch)
                    .getLeaves(mapLeaves)
                    .addNewContractNodeBuilder(MoneyNodeBranchLoaderTest.this::contractNodeProvider)
                    .addNewContractNodeLoader(MoneyNodeBranchLoaderTest.this::contractNodeLoader)
                    .addResponsibilityLoader(MoneyNodeBranchLoaderTest.this::tailResponsibilityLoader)
                    .addUpperNodeLoader(MoneyNodeBranchLoaderTest.this::upperNodeLoader)
                    .build();

            validateContext validator = new validateContext(originalBranch)
                    .updateBranches(mergedBranch.getBranch(), newBranch).addExaminedBranch(mergedBranch);

            // Assert
            assertTrue(validator.validRoot(), "New contract should become root");
            assertTrue(validator.validTail(), "New branch should become tail");
            assertTrue(validator.validResponsibilitySourceType(), "All responsibility must have it source being contract node");
            assertTrue(validator.validResponsibilityPriorityOrder(), "Responsibilities should maintain priority order");
            assertTrue(validator.validRewiredResponsibility(), "Responsibilities should be properly rewired");

        }

        @Test
        @DisplayName("Should merge single contract as new root the second case")
        void mergeSingleContractAsNewRootKeepPriority() {
            // Arrange
            List<StakeHolder> originalParticipants = Arrays.asList(
                    topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)
            );

            List<StakeHolder> newParticipants = Arrays.asList(
                    topExecutor,
                    TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)
            );

            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,
                    originalParticipants.stream()
                            .filter(p -> !p.equals(topExecutor))
                            .toArray(StakeHolder[]::new)
            ));

            // Create original contract with lower priority
            IpBasedContract originalContract = TestDataFactory.genIpBasedContract(
                    topExecutor, originalParticipants, ip, ipBasedContractCounter, participantCounter, 4
            );

            // Create new contract with higher priority
            IpBasedContract newContract = TestDataFactory.genIpBasedContract(
                    topExecutor, newParticipants, ip, ipBasedContractCounter, participantCounter, 6
            );

            // Set up contract nodes and responsibilities
            List<ContractNode> allNodes = TestDataFactory.genContractNodes(
                    Collections.singleton(originalContract),
                    moneyNodeCounter,
                    responsibilityCounter,
                    mapLeaves.get(topExecutor)
            );

            contractNodeMap = allNodes.stream().collect(Collectors.toMap(
                    node -> (IpBasedContract) node.getContract(),
                    node -> node
            ));

            allResponsibilities = allNodes.stream()
                    .map(ContractNode::getDownStreamEdges)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            populateResponsibilityMap();

            // Create and merge branches
            Branch originalBranch = new Branch(Collections.singleton(originalContract));
            Branch newBranch = new Branch(Collections.singleton(newContract));

            // Act
            MoneyNodeBranch mergedBranch = new MoneyNodeBranch.Loader(originalBranch)
                    .addBranch(newBranch)
                    .getLeaves(mapLeaves)
                    .addNewContractNodeBuilder(MoneyNodeBranchLoaderTest.this::contractNodeProvider)
                    .addNewContractNodeLoader(MoneyNodeBranchLoaderTest.this::contractNodeLoader)
                    .addResponsibilityLoader(MoneyNodeBranchLoaderTest.this::tailResponsibilityLoader)
                    .addUpperNodeLoader(MoneyNodeBranchLoaderTest.this::upperNodeLoader)
                    .build();

            validateContext validator = new validateContext(originalBranch)
                    .updateBranches(mergedBranch.getBranch(), newBranch).addExaminedBranch(mergedBranch);

            // Assert
            assertTrue(validator.validRoot(), "New contract should become root");
            assertTrue(validator.validTail(), "New branch should become tail");
            assertTrue(validator.validResponsibilitySourceType(), "All responsibility must have it source being contract node");
            assertTrue(validator.validResponsibilityPriorityOrder(), "Responsibilities should maintain priority order");
            assertTrue(validator.validRewiredResponsibility(), "Responsibilities should be properly rewired");
        }
    }

    @Nested
    @DisplayName("Multiple Contract Merge Tests")
    class MultipleContractMergeTests {

        @Test
        @DisplayName("Should merge multiple contracts with interleaved priorities")
        void mergeMultipleContractsInterleaved() {
            // Arrange

            // Create original branch contracts with priorities 6,4,2
            MultiContractParticipants info = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                    2,3, topExecutor);
            List<List<StakeHolder>> contractsParticipants = info.allContractsParticipant();
            List<StakeHolder> nonExecutorParticipants = info.nonExecutorParticipants();
            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,nonExecutorParticipants.stream()
                    .filter(p -> !p.equals(topExecutor))
                    .toArray(StakeHolder[]::new)));

            Map<List<StakeHolder>, Integer> originalParticipantMap = new HashMap<>();
            originalParticipantMap.put(contractsParticipants.get(0), 6);
            originalParticipantMap.put(contractsParticipants.get(1), 4);
            originalParticipantMap.put(contractsParticipants.get(2), 2);

            List<IpBasedContract> originalContracts = TestDataFactory.genBranchContractsWithPriorities(
                    topExecutor,
                    originalParticipantMap,
                    ip,
                    ipBasedContractCounter,
                    participantCounter
            );

            // Create new branch contracts with priorities 5,3,1
            MultiContractParticipants newContractInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                    2,3, topExecutor);
            List<List<StakeHolder>> newContractsParticipants = newContractInfo.allContractsParticipant();
            List<StakeHolder> newNonExecutorParticipants = info.nonExecutorParticipants();
            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,nonExecutorParticipants.stream()
                    .filter(p -> !p.equals(topExecutor))
                    .toArray(StakeHolder[]::new)));


            Map<List<StakeHolder>, Integer> newParticipantMap = new HashMap<>();
            newParticipantMap.put(newContractsParticipants.get(0), 5);
            newParticipantMap.put(newContractsParticipants.get(1), 3);
            newParticipantMap.put(newContractsParticipants.get(2), 1);

            List<IpBasedContract> newContracts = TestDataFactory.genBranchContractsWithPriorities(
                    topExecutor,
                    newParticipantMap,
                    ip,
                    ipBasedContractCounter,
                    participantCounter
            );

            // Set up contract nodes and responsibilities
            List<ContractNode> allNodes = TestDataFactory.genContractNodes(
                    originalContracts,
                    moneyNodeCounter,
                    responsibilityCounter,
                    mapLeaves.get(topExecutor)
            );

            contractNodeMap = allNodes.stream().collect(Collectors.toMap(
                    node -> (IpBasedContract) node.getContract(),
                    node -> node
            ));

            allResponsibilities = allNodes.stream()
                    .map(ContractNode::getDownStreamEdges)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            populateResponsibilityMap();

            // Create and merge branches
            Branch originalBranch = new Branch(new HashSet<>(originalContracts));
            Branch newBranch = new Branch(new HashSet<>(newContracts));



            // Act
            MoneyNodeBranch mergedBranch = new MoneyNodeBranch.Loader(originalBranch)
                    .addBranch(newBranch)
                    .getLeaves(mapLeaves)
                    .addNewContractNodeBuilder(MoneyNodeBranchLoaderTest.this::contractNodeProvider)
                    .addNewContractNodeLoader(MoneyNodeBranchLoaderTest.this::contractNodeLoader)
                    .addResponsibilityLoader(MoneyNodeBranchLoaderTest.this::tailResponsibilityLoader)
                    .addUpperNodeLoader(MoneyNodeBranchLoaderTest.this::upperNodeLoader)
                    .build();

            // Assert
            validateContext validator = new validateContext(originalBranch)
                    .updateBranches(mergedBranch.getBranch(), newBranch).addExaminedBranch(mergedBranch)
                    .addExaminedBranch(mergedBranch);
            assertTrue(validator.validRoot(), "Correct root node after merge");
            assertTrue(validator.validTail(), "New branch should become tail");
            assertTrue(validator.validResponsibilitySourceType(), "All responsibility must have it source being contract node");
            assertTrue(validator.validResponsibilityPriorityOrder(), "Valid responsibility priority order");
            assertTrue(validator.validRewiredResponsibility(), "Properly rewired responsibilities");
        }

        @Test
        @DisplayName("Should merge when new branch becomes both root and tail")
        void mergeBranchAsRootAndTail() {
            // Arrange
            MultiContractParticipants info = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                    2,3, topExecutor);
            List<List<StakeHolder>> contractsParticipants = info.allContractsParticipant();
            List<StakeHolder> nonExecutorParticipants = info.nonExecutorParticipants();
            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,nonExecutorParticipants.stream()
                    .filter(p -> !p.equals(topExecutor))
                    .toArray(StakeHolder[]::new)));

            Map<List<StakeHolder>, Integer> originalParticipantMap = new HashMap<>();
            originalParticipantMap.put(contractsParticipants.get(0), 5);
            originalParticipantMap.put(contractsParticipants.get(1), 3);

            List<IpBasedContract> originalContracts = TestDataFactory.genBranchContractsWithPriorities(
                    topExecutor,
                    originalParticipantMap,
                    ip,
                    ipBasedContractCounter,
                    participantCounter
            );

            // Create new branch contracts with priorities 5,3,1
            MultiContractParticipants newContractInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                    2,3, topExecutor);
            List<List<StakeHolder>> newContractsParticipants = newContractInfo.allContractsParticipant();
            List<StakeHolder> newNonExecutorParticipants = info.nonExecutorParticipants();
            mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,nonExecutorParticipants.stream()
                    .filter(p -> !p.equals(topExecutor))
                    .toArray(StakeHolder[]::new)));


            Map<List<StakeHolder>, Integer> newParticipantMap = new HashMap<>();
            newParticipantMap.put(newContractsParticipants.get(0), 6);
            newParticipantMap.put(newContractsParticipants.get(1), 2);

            List<IpBasedContract> newContracts = TestDataFactory.genBranchContractsWithPriorities(
                    topExecutor,
                    newParticipantMap,
                    ip,
                    ipBasedContractCounter,
                    participantCounter
            );

            // Setup contract nodes and responsibilities
            List<ContractNode> allNodes = TestDataFactory.genContractNodes(
                    originalContracts,
                    moneyNodeCounter,
                    responsibilityCounter,
                    mapLeaves.get(topExecutor)
            );

            contractNodeMap = allNodes.stream().collect(Collectors.toMap(
                    node -> (IpBasedContract) node.getContract(),
                    node -> node
            ));

            allResponsibilities = allNodes.stream()
                    .map(ContractNode::getDownStreamEdges)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            populateResponsibilityMap();

            // Create and merge branches
            Branch originalBranch = new Branch(new HashSet<>(originalContracts));
            Branch newBranch = new Branch(new HashSet<>(newContracts));

            // Act
            MoneyNodeBranch mergedBranch = new MoneyNodeBranch.Loader(originalBranch)
                    .addBranch(newBranch)
                    .getLeaves(mapLeaves)
                    .addNewContractNodeBuilder(MoneyNodeBranchLoaderTest.this::contractNodeProvider)
                    .addNewContractNodeLoader(MoneyNodeBranchLoaderTest.this::contractNodeLoader)
                    .addResponsibilityLoader(MoneyNodeBranchLoaderTest.this::tailResponsibilityLoader)
                    .addUpperNodeLoader(MoneyNodeBranchLoaderTest.this::upperNodeLoader)
                    .build();

            // Assert
            validateContext validator = new validateContext(originalBranch)
                    .updateBranches(mergedBranch.getBranch(), newBranch).addExaminedBranch(mergedBranch)
                    .addExaminedBranch(mergedBranch);

            assertTrue(validator.validRoot(), "New branch should become root");
            assertTrue(validator.validTail(), "New branch should become tail");
            assertTrue(validator.validResponsibilitySourceType(), "All responsibility must have it source being contract node");
            assertTrue(validator.validResponsibilityPriorityOrder(), "Valid responsibility priority order");
            assertTrue(validator.validRewiredResponsibility(), "Properly rewired responsibilities");
        }
    }
}
