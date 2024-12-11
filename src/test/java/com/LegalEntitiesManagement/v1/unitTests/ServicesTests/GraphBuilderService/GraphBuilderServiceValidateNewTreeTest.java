package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.GraphBuilderService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.ContractNodeService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.IpTreeService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.ResponsibilityService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.StakeHolderLeafService;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GraphBuilderServiceValidateNewTreeTest {
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


    @BeforeEach
    void setUp(){
        Role topExecutorRole = new Role("Top Executor", "Mock top executor", 6);
        topExecutorRole.setId(1L);

        Role secondExecutorRole = new Role("Second Executor", "Mock Second executor", 5);
        secondExecutorRole.setId(2L);

        Role thirdExecutorRole = new Role("Second Executor", "Mock Second executor", 4);
        thirdExecutorRole.setId(1L);

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
    }

    @Test
    @DisplayName("Should throw errors when having overlapped non executor stakeHolder")
    void createTreeWithOverlapNonExecutorStakeHolder(){
        // Arrange
        StakeHolder nonExecutor = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);
        IpBasedContract firstContract = TestDataFactory.genIpBasedContract(topExecutor, Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract secondContract = TestDataFactory.genIpBasedContract(topExecutor, Arrays.asList(topExecutor, nonExecutor),
                ip, ipBasedContractCounter, participantCounter, 5);

        IpBasedContract thirdContract = TestDataFactory.genIpBasedContract(secondExecutor, Arrays.asList(secondExecutor, nonExecutor),
                ip, ipBasedContractCounter, participantCounter, 4);

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateBuildNewTree(Arrays.asList(firstContract, secondContract, thirdContract))
        );
        assertTrue(exception.getMessage().contains("Stakeholders appear in multiple branch:"));
    }

    @Test
    @DisplayName("Should throw error when lower priority executor targets higher priority executor")
    void validateBranchesPriorityFailure() {
        // Arrange
        // Create a contract where a lower priority executor (thirdExecutor) targets a higher priority executor (topExecutor)
        IpBasedContract firstContract = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract secondContract = TestDataFactory.genIpBasedContract(thirdExecutor,
                Arrays.asList(thirdExecutor, topExecutor), // This creates the invalid priority relationship
                ip, ipBasedContractCounter, participantCounter, 4);

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateBuildNewTree(Arrays.asList(firstContract, secondContract))
        );
        assertTrue(exception.getMessage().contains("having higher priority executors as its target"));
    }

    @Test
    @DisplayName("Should accept valid branch priorities")
    void validateBranchesPrioritySuccess() {
        // Arrange
        // Create contracts with valid priority relationships (higher priority targets lower priority)
        IpBasedContract firstContract = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract secondContract = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, thirdExecutor),
                ip, ipBasedContractCounter, participantCounter, 5);

        // Act & Assert
        assertDoesNotThrow(() ->
                graphBuilderService.validateBuildNewTree(Arrays.asList(firstContract, secondContract))
        );
    }

    @Test
    @DisplayName("Should throw error when branches are disconnected")
    void validateConnectedBranchesFailure() {
        // Arrange
        // Create two completely disconnected branches
        StakeHolder participant1 = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);
        StakeHolder participant2 = TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter);

        IpBasedContract firstContract = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, participant1),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract secondContract = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, participant2),
                ip, ipBasedContractCounter, participantCounter, 5);

        // Assert
        ContractValidationFailed exception = assertThrows(ContractValidationFailed.class, () ->
                graphBuilderService.validateBuildNewTree(Arrays.asList(firstContract, secondContract))
        );
        assertTrue(exception.getMessage().contains("do not connect to each other"));
    }

    @Test
    @DisplayName("Should accept connected branches")
    void validateConnectedBranchesSuccess() {
        // Arrange
        // Create connected branches where one executor is a participant in another contract
        IpBasedContract firstContract = TestDataFactory.genIpBasedContract(topExecutor,
                Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 6);

        IpBasedContract secondContract = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, thirdExecutor),
                ip, ipBasedContractCounter, participantCounter, 5);

        // Act & Assert
        assertDoesNotThrow(() ->
                graphBuilderService.validateBuildNewTree(Arrays.asList(firstContract, secondContract))
        );
    }
}
