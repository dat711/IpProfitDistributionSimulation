package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.ResponsibilityDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ResponsibilityNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.ResponsibilityRepository;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.ResponsibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class ResponsibilityServiceTest extends BaseServiceTestMockedDependencies {
    private ResponsibilityService responsibilityService;
    private Responsibility testResponsibility;
    private ResponsibilityDto testResponsibilityDto;
    private ContractNode testSourceNode;
    private StakeHolderLeaf testTargetNode;
    private Contract testContract;
    private StakeHolder testExecutor;
    private StakeHolder testStakeHolder;
    private Role testRole;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        responsibilityService = new ResponsibilityService(responsibilityRepository);

        // Create test role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        // Create stakeholders
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);
        testStakeHolder = new StakeHolder("Test StakeHolder", testRole);
        testStakeHolder.setId(2L);

        // Create contract
        testContract = new Contract("Test Contract", LocalDate.now(), 1, testExecutor);
        testContract.setId(1L);

        // Create source node (ContractNode)
        testSourceNode = new ContractNode(testContract);
        testSourceNode.setId(1L);

        // Create target node (StakeHolderLeaf)
        testTargetNode = new StakeHolderLeaf(testStakeHolder);
        testTargetNode.setId(2L);

        // Create responsibility
        testResponsibility = new Responsibility(testTargetNode, testSourceNode, 50.0);
        testResponsibility.setId(1L);

        // Create responsibility DTO
        testResponsibilityDto = new ResponsibilityDto(1L, 2L, 1L, 50.0);
    }

    @Test
    @DisplayName("Should find Responsibility by ID")
    void findById_ExistingId_ReturnsResponsibility() {
        when(responsibilityRepository.findById(1L)).thenReturn(Optional.of(testResponsibility));

        Responsibility found = responsibilityService.findById(1L);

        assertNotNull(found);
        assertEquals(testResponsibility.getId(), found.getId());
        assertEquals(testResponsibility.getSource().getId(), found.getSource().getId());
        assertEquals(testResponsibility.getTarget().getId(), found.getTarget().getId());
        assertEquals(testResponsibility.getPercentage(), found.getPercentage());
        verify(responsibilityRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when Responsibility not found")
    void findById_NonExistingId_ThrowsException() {
        when(responsibilityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponsibilityNotFoundException.class, () -> responsibilityService.findById(99L));
        verify(responsibilityRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save Responsibility")
    void save_ValidResponsibility_ReturnsSavedResponsibility() {
        when(responsibilityRepository.save(any(Responsibility.class))).thenReturn(testResponsibility);

        Responsibility saved = responsibilityService.save(testResponsibility);

        assertNotNull(saved);
        assertEquals(testResponsibility.getId(), saved.getId());
        assertEquals(testResponsibility.getSource().getId(), saved.getSource().getId());
        assertEquals(testResponsibility.getTarget().getId(), saved.getTarget().getId());
        assertEquals(testResponsibility.getPercentage(), saved.getPercentage());
        verify(responsibilityRepository).save(testResponsibility);
    }

    @Test
    @DisplayName("Should save Responsibility from DTO")
    void saveFromDto_ValidDto_ReturnsSavedResponsibility() {
        when(responsibilityRepository.save(any(Responsibility.class))).thenReturn(testResponsibility);

        Responsibility saved = responsibilityService.saveFromDto(testResponsibilityDto);

        assertNotNull(saved);
        assertEquals(testResponsibilityDto.getId(), saved.getId());
        assertEquals(testResponsibilityDto.getSourceNodeId(), saved.getSource().getId());
        assertEquals(testResponsibilityDto.getTargetNodeId(), saved.getTarget().getId());
        assertEquals(testResponsibilityDto.getPercentage(), saved.getPercentage());
        verify(responsibilityRepository).save(any(Responsibility.class));
    }

    @Test
    @DisplayName("Should find Responsibility by source and target")
    void findBySourceAndTarget_ReturnsResponsibility() {
        when(responsibilityRepository.findBySourceAndTarget(1L, 2L))
                .thenReturn(Optional.of(testResponsibility));

        Optional<Responsibility> found = responsibilityService.findBySourceAndTarget(1L, 2L);

        assertTrue(found.isPresent());
        assertEquals(testResponsibility.getId(), found.get().getId());
        assertEquals(testResponsibility.getPercentage(), found.get().getPercentage());
        verify(responsibilityRepository).findBySourceAndTarget(1L, 2L);
    }

    @Test
    @DisplayName("Should find downstream edges")
    void findDownstreamEdges_ReturnsEdges() {
        Set<Responsibility> edges = new HashSet<>(Arrays.asList(testResponsibility));
        when(responsibilityRepository.findDownstreamEdges(1L)).thenReturn(edges);

        Set<Responsibility> found = responsibilityService.findDownstreamEdges(1L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testResponsibility));
        verify(responsibilityRepository).findDownstreamEdges(1L);
    }

    @Test
    @DisplayName("Should find upstream edges")
    void findUpstreamEdges_ReturnsEdges() {
        Set<Responsibility> edges = new HashSet<>(Arrays.asList(testResponsibility));
        when(responsibilityRepository.findUpstreamEdges(2L)).thenReturn(edges);

        Set<Responsibility> found = responsibilityService.findUpstreamEdges(2L);

        assertNotNull(found);
        assertEquals(1, found.size());
        assertTrue(found.contains(testResponsibility));
        verify(responsibilityRepository).findUpstreamEdges(2L);
    }

    @Test
    @DisplayName("Should verify existing responsibility")
    void verify_ExistingResponsibility_NoException() {
        when(responsibilityRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> responsibilityService.verify(testResponsibility));
        verify(responsibilityRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing responsibility during verification")
    void verify_NonExistingResponsibility_ThrowsException() {
        when(responsibilityRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponsibilityNotFoundException.class, () -> responsibilityService.verify(testResponsibility));
        verify(responsibilityRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for invalid type during verification")
    void verify_InvalidType_ThrowsException() {
        String invalidObject = "Invalid Type";

        assertThrows(TypeNotMatchException.class, () -> responsibilityService.verify(invalidObject));
    }

    @Test
    @DisplayName("Should find all responsibilities")
    void findAll_ReturnsAllResponsibilities() {
        Responsibility responsibility2 = new Responsibility(testTargetNode, testSourceNode, 30.0);
        responsibility2.setId(2L);
        List<Responsibility> responsibilities = Arrays.asList(testResponsibility, responsibility2);

        when(responsibilityRepository.findAll()).thenReturn(responsibilities);

        List<Responsibility> result = responsibilityService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(responsibilityRepository).findAll();
    }

    @Test
    @DisplayName("Should handle getQuantity method correctly")
    void getQuantity_ReturnsPercentage() {
        assertEquals(50.0, testResponsibility.getQuantity());
    }
}
