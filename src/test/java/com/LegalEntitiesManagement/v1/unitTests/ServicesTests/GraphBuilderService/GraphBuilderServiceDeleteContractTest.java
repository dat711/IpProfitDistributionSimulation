package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class GraphBuilderServiceDeleteContractTest extends GraphBuilderServiceMockDependencies {
    @BeforeEach
    @Override
    void setUp(){
        super.setUp();
        setUpMockBehaviorsForDelete();
        setNewTree();
    }

    @Test
    @DisplayName("Should allow delete valid contracts in the default set up")
    void deleteValidContract(){
        // Arrange
        IpBasedContract toDelete = originalContracts.get(3);
        Set<ContractParticipant> toDeleteParticipants = toDelete.getContractParticipants();

        doAnswer(invocationOnMock -> {
            IpTree ipTree = new IpTree();
            ipTree.setRootContractNode(treeRoot);
            ipTree.setId(1L);
            ipTree.setIntellectualProperty(ip);
            return Optional.of(ipTree);
        }).when(ipTreeService).findByIntellectualPropertyId(anyLong());

        // Act
        graphBuilderService.updateDeleteContract(toDelete);
        FullTreeValidationContext context = new FullTreeValidationContext();
        context.setMapLeaves(mapLeaves);
        context.setHeadNode(treeRoot);
        context.setExpectedChanges(
                new FullTreeValidationContext.ExpectedChanges(toDeleteParticipants, null, null,
                        toDelete, null, null)
        );
        context.populateContext();

        // Assert
        assertTrue(context.validResponsibilities(), "Responsibilities should remain valid");
        assertFalse(context.contractIsNotDeleted(), "Contract should already be deleted");
        assertFalse(context.participantsAreNotDeleted(),"Participants should already be deleted");
    }
}
