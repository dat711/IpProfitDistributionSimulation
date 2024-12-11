package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.*;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.dto.*;
import com.LegalEntitiesManagement.v1.Entities.exceptions.*;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class StakeHolderLeafServiceTest extends BaseServiceTestMockedDependencies {
    private StakeHolderLeafService leafService;
    private StakeHolder testStakeHolder;
    private StakeHolderLeaf testLeaf;
    private StakeHolderLeafDto testLeafDto;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        leafService = new StakeHolderLeafService(stakeHolderLeafRepository);

        // Create test data
        Role role = new Role("Test Role", "Test Description", 1);
        role.setId(1L);
        testStakeHolder = new StakeHolder("Test StakeHolder", role);
        testStakeHolder.setId(1L);

        testLeaf = new StakeHolderLeaf(testStakeHolder);
        testLeaf.setId(1L);

        testLeafDto = new StakeHolderLeafDto(1L, 1L);
    }

    @Test
    @DisplayName("Should find StakeHolderLeaf by ID")
    void findById_ExistingId_ReturnsLeaf() {
        when(stakeHolderLeafRepository.findById(1L)).thenReturn(Optional.of(testLeaf));

        StakeHolderLeaf found = leafService.findById(1L);

        assertNotNull(found);
        assertEquals(testLeaf.getId(), found.getId());
        assertEquals(testStakeHolder.getId(), found.getStakeHolder().getId());
        verify(stakeHolderLeafRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when StakeHolderLeaf not found")
    void findById_NonExistingId_ThrowsException() {
        when(stakeHolderLeafRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StakeHolderLeafNotFoundException.class, () -> leafService.findById(99L));
        verify(stakeHolderLeafRepository).findById(99L);
    }



    @Test
    @DisplayName("Should find leaf nodes for contract node")
    void findLeafNodesForContractNode_ReturnsLeaves() {
        Set<StakeHolderLeaf> leaves = new HashSet<>(Collections.singletonList(testLeaf));
        when(stakeHolderLeafRepository.findLeafNodesForContractNode(1L)).thenReturn(leaves);

        Set<StakeHolderLeaf> found = leafService.findLeafNodesForContractNode(1L);

        assertEquals(1, found.size());
        assertTrue(found.contains(testLeaf));
        verify(stakeHolderLeafRepository).findLeafNodesForContractNode(1L);
    }

}
