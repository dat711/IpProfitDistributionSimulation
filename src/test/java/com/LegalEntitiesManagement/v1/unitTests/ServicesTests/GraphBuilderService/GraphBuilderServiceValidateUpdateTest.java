package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.services.GraphBuilderService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GraphBuilderServiceValidateUpdateTest {
    @Mock
    IpTreeService ipTreeService;

    @Mock
    ContractNodeService contractNodeService;

    @Mock
    StakeHolderLeafService stakeHolderLeafService;

    @Mock
    ResponsibilityService responsibilityService;

    private Role participantRole;
    private Counter stakeHolderCounter, ipBasedContractCounter, participantCounter;
    private StakeHolder topExecutor, secondExecutor, thirdExecutor;
    private IntellectualProperty ip;
    private GraphBuilderService graphBuilderService;
    private List<IpBasedContract> treeContracts;

    @BeforeEach
    void setUp() {
        Role topExecutorRole = new Role("Top Executor", "Mock top executor", 6);
        topExecutorRole.setId(1L);

        Role secondExecutorRole = new Role("Second Executor", "Mock Second executor", 5);
        secondExecutorRole.setId(2L);

        Role thirdExecutorRole = new Role("Third Executor", "Mock Third executor", 4);
        thirdExecutorRole.setId(3L);

        participantRole = new Role("Participant role", "Mock participant", 1);
        participantRole.setId(4L);

        ip = new IntellectualProperty();
        ip.setName("Test Ip");
        ip.setId(1L);

        stakeHolderCounter = new Counter();
        ipBasedContractCounter = new Counter();
        participantCounter = new Counter();

        topExecutor = TestDataFactory.genStakeHolder(topExecutorRole, stakeHolderCounter);
        secondExecutor = TestDataFactory.genStakeHolder(secondExecutorRole, stakeHolderCounter);
        thirdExecutor = TestDataFactory.genStakeHolder(thirdExecutorRole, stakeHolderCounter);

        graphBuilderService = new GraphBuilderService(ipTreeService, contractNodeService, stakeHolderLeafService, responsibilityService);

        // Create base tree contracts
        treeContracts = createBaseTreeContracts();
    }

    private List<IpBasedContract> createBaseTreeContracts() {
        StakeHolder participant1 = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);
        StakeHolder participant2 = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);

        IpBasedContract contract1 = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, secondExecutor, participant1),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract contract2 = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, thirdExecutor, participant2),
                ip, ipBasedContractCounter, participantCounter, 5);

        return Arrays.asList(contract1, contract2);
    }

    @Test
    @DisplayName("Should reject update with executor change")
    void shouldRejectExecutorChange() {
        // Arrange
        IpBasedContract originalContract = treeContracts.get(0);

        // Create new contract with different executor
        IpBasedContract updatedContract = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, topExecutor),
                ip, ipBasedContractCounter, participantCounter, 6);
        updatedContract.setId(originalContract.getId());

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateUpdateContract(updatedContract, treeContracts)
        );
        assertTrue(exception.getMessage().contains("executor is forbidden"));
    }

    @Test
    @DisplayName("Should reject update with duplicate priority in same branch")
    void shouldRejectDuplicatePriority() {
        // Arrange : original tree set up

        TestDataFactory.MultiContractParticipants info = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                2,3, topExecutor);

        List<List<StakeHolder>> contractsParticipants = info.allContractsParticipant();
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

        // Create new contract with duplicate priority
        IpBasedContract updatedContract = TestDataFactory.genIpBasedContract(topExecutor, contractsParticipants.get(1), ip,
                ipBasedContractCounter, participantCounter, 5);
        updatedContract.setId(originalContracts.get(1).getId());


        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateUpdateContract(updatedContract, originalContracts)
        );

        assertTrue(exception.getMessage().contains("same priority with other on the same branch"));
    }

    @Test
    @DisplayName("Should reject update with affected downstream edges")
    void shouldRejectAffectedDownstreamEdges() {
        // Arrange
        IpBasedContract originalContract = treeContracts.get(0);
        // Get an existing non-executor from another branch
        StakeHolder existingNonExecutor = treeContracts.get(1).getContractParticipants().stream()
                .filter(p -> p.getStakeholder().getId() > 3)
                .findFirst()
                .orElseThrow()
                .getStakeholder();

        // Create updated contract trying to use non-executor from another branch
        IpBasedContract updatedContract = TestDataFactory.genIpBasedContract(
                topExecutor,  // Keep same executor
                Arrays.asList(topExecutor, existingNonExecutor), // Try to use non-executor from other branch
                ip,
                ipBasedContractCounter,
                participantCounter,
                6
        );
        updatedContract.setId(originalContract.getId());

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateUpdateContract(updatedContract, treeContracts)
        );

        assertTrue(exception.getMessage().contains("is executor of other contracts branch"));
    }

    @Test
    @DisplayName("Should reject update with overlapping non-executor")
    void shouldRejectOverlappingNonExecutor(){
        IpBasedContract originalContract = treeContracts.get(0);

        StakeHolder existingNonExecutor = treeContracts.get(1).getContractParticipants().stream()
                .filter(p -> p.getStakeholder().getId() > 3)
                .findFirst()
                .orElseThrow()
                .getStakeholder();

        Set<StakeHolder> stakeHolders = originalContract.getContractParticipants().stream()
                .map(ContractParticipant::getStakeholder).collect(Collectors.toSet());

        stakeHolders.add(existingNonExecutor);

        IpBasedContract updatedContract = TestDataFactory.genIpBasedContract(
                topExecutor,  // Keep same executor
                stakeHolders.stream().toList(), // Try to use non-executor from other branch
                ip,
                ipBasedContractCounter,
                participantCounter,
                6
        );
        updatedContract.setId(originalContract.getId());

        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateUpdateContract(updatedContract, treeContracts)
        );
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("duplicated non-executors with other branches"));

    }


    @Test
    @DisplayName("Should accept valid update")
    void shouldAcceptValidUpdate() {
        // Arrange
        IpBasedContract originalContract = treeContracts.get(0);
        StakeHolder newParticipant = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);

        // Create valid updated contract
        IpBasedContract updatedContract = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, secondExecutor, newParticipant),
                ip, ipBasedContractCounter, participantCounter, 6);
        updatedContract.setId(originalContract.getId());

        // Assert
        assertDoesNotThrow(() ->
                graphBuilderService.validateUpdateContract(updatedContract, treeContracts)
        );
    }
}
