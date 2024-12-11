package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class GraphBuilderServiceBuildNewTreeTest extends GraphBuilderServiceMockDependencies{
    @BeforeEach
    @Override
    void setUp(){
        super.setUp();
        setUpMockBehaviorsForBuildNewTree();
    }

    @Test
    @DisplayName("Should create the tree from default contracts")
    void createNewTree(){
        Set<ContractParticipant> allParticipants = originalContracts.stream().map(Contract::getContractParticipants)
                .flatMap(Set::stream).collect(Collectors.toSet());
        FullTreeValidationContext context = new FullTreeValidationContext();
        context.setExpectedChanges(
                new FullTreeValidationContext.ExpectedChanges(null, allParticipants, null,
                        null, new HashSet<>(originalContracts), null)
        );

        IpTree ipTree = graphBuilderService.buildNewTree(originalContracts);
        context.setHeadNode(ipTree.getRootContractNode());
        context.setMapLeaves(mapLeaves);
        context.populateContext();
        assertFalse(context.contractsAreNotAdded(), "Should add new contracts");
        assertFalse(context.participantsAreNotAdded(), "Should add new participants");
        assertTrue(context.validResponsibilities(), "The responsibilities should be valid");
    }
}
