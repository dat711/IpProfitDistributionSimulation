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

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class GraphBuilderServiceInsertNewContractIntoExistedTreeTest extends GraphBuilderServiceMockDependencies{

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        setNewTree();
        setUpMockBehaviorsForInsertToExistedTree();
    }

    @Test
    @DisplayName("Should allow add valid contract")
    void shouldAllowAddContract(){
        IpBasedContract toAdd = TestDataFactory.genIpBasedContract(secondExecutor,
                Arrays.asList(secondExecutor, TestDataFactory.genStakeHolder(participantRole, stakeHolderCounter)),
                ip, ipBasedContractCounter, participantCounter, 3 );

        ArrayList<IpBasedContract> newContracts = new ArrayList<>();
        newContracts.add(toAdd);

        Set<ContractParticipant> toAddParticipant = toAdd.getContractParticipants();

        mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter, toAddParticipant.stream()
                .map(ContractParticipant::getStakeholder).filter(stakeHolder -> !stakeHolder.equals(secondExecutor))
                .toArray(StakeHolder[]::new)));

        graphBuilderService.insertContractsToExistedTree(newContracts, originalContracts);

        TestDataFactory.FullTreeValidationContext context = new TestDataFactory.FullTreeValidationContext();
        context.setMapLeaves(mapLeaves);
        context.setHeadNode(treeRoot);
        context.setExpectedChanges(
                new TestDataFactory.FullTreeValidationContext.ExpectedChanges(null, toAddParticipant, null,
                        null, new HashSet<>(newContracts), null)
        );
        context.populateContext();

        // Assert
        assertFalse(context.participantsAreNotAdded(), "New participants should already be added");
        assertFalse(context.contractsAreNotAdded(), "Supposed adding contract should already be added");
    }
}
