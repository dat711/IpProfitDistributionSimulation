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

import static org.junit.jupiter.api.Assertions.*;
public class GraphBuilderServiceTestValidateAddingNewContractToExistedTree {

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

    private ArrayList<IpBasedContract> originalContracts;

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
        generateOriginalContracts();
    }

    private void generateOriginalContracts(){
        TestDataFactory.MultiContractParticipants topExecutorContractsInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                2,3, topExecutor);
        List<List<StakeHolder>> topExecutorContractsParticipants = topExecutorContractsInfo.allContractsParticipant();
        Map<List<StakeHolder>, Integer> topBranchOriginalParticipantMap = new HashMap<>();
        topBranchOriginalParticipantMap.put(topExecutorContractsParticipants.get(0), 6);
        topBranchOriginalParticipantMap.put(topExecutorContractsParticipants.get(1), 5);

        originalContracts = new ArrayList<>(TestDataFactory.genBranchContractsWithPriorities(
                topExecutor,
                topBranchOriginalParticipantMap,
                ip,
                ipBasedContractCounter,
                participantCounter
        ));

        IpBasedContract connectToSecondExecutor = TestDataFactory.genIpBasedContract(topExecutor, Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 4);
        originalContracts.add(connectToSecondExecutor);
        // gen second branchesOfOriginalTree
        TestDataFactory.MultiContractParticipants secondExecutorContractsInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                2,3, secondExecutor);
        List<List<StakeHolder>> secondExecutorContractsParticipants = secondExecutorContractsInfo.allContractsParticipant();

        Map<List<StakeHolder>, Integer> secondBranchOriginalParticipantMap = new HashMap<>();
        secondBranchOriginalParticipantMap.put(secondExecutorContractsParticipants.get(0), 6);
        secondBranchOriginalParticipantMap.put(secondExecutorContractsParticipants.get(1), 5);
//
        originalContracts.addAll(TestDataFactory.genBranchContractsWithPriorities(
                secondExecutor,
                secondBranchOriginalParticipantMap,
                ip,
                ipBasedContractCounter,
                participantCounter
        ));
    }

    @Test
    @DisplayName("Should Reject adding contracts that have overlap non-executor")
    void shouldRejectContractsHavingOverlapNonExecutors(){
        // generate original tree contracts
        StakeHolder overlapNonExecutor = originalContracts.get(1).getContractParticipants().stream().map(ContractParticipant::getStakeholder)
                .filter(stakeHolder -> !stakeHolder.equals(topExecutor)).findFirst().orElseThrow();

        // Create new contract with duplicate non Executor
        List<IpBasedContract> newContracts = new ArrayList<>();
        newContracts.add(TestDataFactory.genIpBasedContract(secondExecutor, Arrays.asList(secondExecutor, overlapNonExecutor), ip,
                ipBasedContractCounter, participantCounter, 4));

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateAddNewContractToExistedTree(newContracts, originalContracts)
        );

        assertTrue(exception.getMessage().contains("The new batch of contracts violate rule of adding middle contractNode"));
    }

    @Test
    @DisplayName("Should Reject adding contracts that have connected group with more than one target being executor of the original group")
    void shouldRejectContractsGroupHavingMultipleOriginalExecutorWithinItsParticipant(){
        // generate original tree contracts
        originalContracts.add(TestDataFactory.genIpBasedContract(topExecutor, Arrays.asList(topExecutor, thirdExecutor), ip,
                ipBasedContractCounter, participantCounter, 3));

        // generate new contracts to add
        List<IpBasedContract> newContracts = new ArrayList<>();
        newContracts.add(TestDataFactory.genIpBasedContract(thirdExecutor, Arrays.asList(thirdExecutor, secondExecutor), ip,
                ipBasedContractCounter, participantCounter, 4));

        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateAddNewContractToExistedTree(newContracts, originalContracts)
        );

        assertTrue(exception.getMessage().contains("The new batch of contracts violate rule of adding middle contractNode"));
    }

    @Test
    @DisplayName("Should Reject adding contracts that have violate unique priority")
    void shouldRejectContractsViolateUniquePriorities(){
        List<IpBasedContract> newContracts = new ArrayList<>();
        newContracts.add(TestDataFactory.genIpBasedContract(secondExecutor, Arrays.asList(secondExecutor,
                TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)), ip,
                ipBasedContractCounter, participantCounter, 5));

        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateAddNewContractToExistedTree(newContracts, originalContracts)
        );
        assertTrue(exception.getMessage().contains("that violate contract priority uniqueness"));
    }

    @Test
    @DisplayName("Should Reject adding contracts that have no connection to original tree")
    void shouldRejectContractsThatIsNotConnectedToOriginalTree(){
        List<IpBasedContract> newContracts = new ArrayList<>();
        newContracts.add(TestDataFactory.genIpBasedContract(thirdExecutor, Arrays.asList(thirdExecutor,
                        TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)), ip,
                ipBasedContractCounter, participantCounter, 5));

        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateAddNewContractToExistedTree(newContracts, originalContracts)
        );
        assertTrue(exception.getMessage().contains("do not connect to the original tree"));
    }
}
