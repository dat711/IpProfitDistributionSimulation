package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ValidateDeleteContractTest extends GraphBuilderServiceMockDependencies {

    @BeforeEach
    @Override
    void setUp(){
        super.setUp();
        setNewTree();
        setUpMockBehaviorsForValidateDelete();
    }

    @Test
    @DisplayName("Should allow delete leaf contractNode")
    void shouldAllowDeleteLeafContractNode(){
        IpBasedContract toDeleteContract = originalContracts.get(4); // last contracts

        assertDoesNotThrow(() -> graphBuilderService.validateDeleteContract(toDeleteContract));
    }

    @Test
    @DisplayName("Should allow delete contractNode having downstream node in the same branch")
    void shouldAllowDeleteContractNodeHavingDownstreamNodeInTheSameBranch(){
        IpBasedContract toDeleteContract = originalContracts.get(3); // top of second branch

        assertDoesNotThrow(() -> graphBuilderService.validateDeleteContract(toDeleteContract));
    }

    @Test
    @DisplayName("Should reject delete contractNode having downstream node in different branch")
    void rejectDeleteContractNodeHavingDownStreamNodeInDifferentBranch(){
        IpBasedContract toDeleteContract = originalContracts.get(2); // connector contract

        Exception exception = assertThrows( ContractValidationFailed.class, () -> graphBuilderService.validateDeleteContract(toDeleteContract));
        assertEquals("Can not delete current contract, check if they have downstream contract with different executor",
                exception.getMessage());
    }
}