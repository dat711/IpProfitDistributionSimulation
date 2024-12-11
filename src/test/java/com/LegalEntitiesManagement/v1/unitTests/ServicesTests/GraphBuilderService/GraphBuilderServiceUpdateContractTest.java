package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(MockitoExtension.class)
public class GraphBuilderServiceUpdateContractTest extends GraphBuilderServiceMockDependencies{
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        setNewTree();
        setUpMockBehaviorsForUpdate();
    }

    @Test
    @DisplayName("Should allow update valid contract")
    void shouldAllowUpdateContract(){
        // Arrange
        IpBasedContract originalOne = originalContracts.get(originalContracts.size() - 1);

        IpBasedContract toUpdate = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)),
                ip, ipBasedContractCounter, participantCounter, 8 );

        toUpdate.setId(originalOne.getId());

        Set<ContractParticipant> toUpdateParticipants = toUpdate.getContractParticipants().stream().filter(
               participant -> participant.getStakeholder().equals(secondExecutor)
        ).collect(Collectors.toSet());

        Set<ContractParticipant> toAddParticipant = new HashSet<>(toUpdate.getContractParticipants());
        toAddParticipant.removeAll(toUpdateParticipants);

        mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter, toAddParticipant.stream()
                .map(ContractParticipant::getStakeholder)
                .toArray(StakeHolder[]::new)));

        Set<ContractParticipant> toDeleteParticipants = originalOne.getContractParticipants().stream().filter(
                participant -> !participant.getStakeholder().equals(secondExecutor)
        ).collect(Collectors.toSet());

        // Act
        graphBuilderService.updateContract(toUpdate, originalContracts);
        TestDataFactory.FullTreeValidationContext context = new TestDataFactory.FullTreeValidationContext();
        context.setMapLeaves(mapLeaves);
        context.setHeadNode(treeRoot);
        context.setExpectedChanges(
                new TestDataFactory.FullTreeValidationContext.ExpectedChanges(toDeleteParticipants, toAddParticipant, toUpdateParticipants,
                        null, null, toUpdate)
        );
        context.populateContext();

        // Assert
        assertFalse(context.contractIsNotDeleted(), "Contract should already be deleted");
        assertFalse(context.participantsAreNotDeleted(),"Supposed delete participants should already be deleted");
        assertFalse(context.participantsAreNotAdded(), "New participants should already be added");
        assertFalse(context.participantsAreNotUpdated(), "Supposed update participants should already be updated");
    }
}
