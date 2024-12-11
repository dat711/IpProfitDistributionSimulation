package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractNodeDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.ContractNodeService;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNodeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class ContractNodeServiceTest extends BaseServiceTestMockedDependencies {
    private ContractNodeService nodeService;
    private ContractNode testNode;
    private ContractNodeDto testNodeDto;
    private Contract testContract;
    private StakeHolder testExecutor;
    private Role testRole;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        nodeService = new ContractNodeService(contractNodeRepository);

        // Create test role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);

        // Create test contract with executor
        testContract = new Contract("Test Contract", LocalDate.now(), 1, testExecutor);
        testContract.setId(1L);

        testNode = new ContractNode(testContract);
        testNode.setId(1L);

        testNodeDto = new ContractNodeDto(1L, 1L);
    }

    @Test
    @DisplayName("Should save ContractNode with executor information")
    void save_ValidNodeWithExecutor_ReturnsSavedNode() {
        when(contractNodeRepository.save(any(ContractNode.class))).thenReturn(testNode);

        ContractNode saved = nodeService.save(testNode);

        assertNotNull(saved);
        assertEquals(testNode.getId(), saved.getId());
        assertEquals(testContract.getId(), saved.getContract().getId());
        assertEquals(testExecutor.getId(), saved.getContract().getExecutor().getId());
        verify(contractNodeRepository).save(testNode);
    }
    @Test
    @DisplayName("Should find ContractNode by ID")
    void findById_ExistingId_ReturnsNode() {
        when(contractNodeRepository.findById(1L)).thenReturn(Optional.of(testNode));

        ContractNode found = nodeService.findById(1L);

        assertNotNull(found);
        assertEquals(testNode.getId(), found.getId());
        assertEquals(testContract.getId(), found.getContract().getId());
        verify(contractNodeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when ContractNode not found")
    void findById_NonExistingId_ThrowsException() {
        when(contractNodeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContractNodeNotFoundException.class, () -> nodeService.findById(99L));
        verify(contractNodeRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save ContractNode")
    void save_ValidNode_ReturnsSavedNode() {
        when(contractNodeRepository.save(any(ContractNode.class))).thenReturn(testNode);

        ContractNode saved = nodeService.save(testNode);

        assertNotNull(saved);
        assertEquals(testNode.getId(), saved.getId());
        assertEquals(testContract.getId(), saved.getContract().getId());
        verify(contractNodeRepository).save(testNode);
    }

    @Test
    @DisplayName("Should save ContractNode from DTO")
    void saveFromDto_ValidDto_ReturnsSavedNode() {
        when(contractNodeRepository.save(any(ContractNode.class))).thenReturn(testNode);

        ContractNode saved = nodeService.saveFromDto(testNodeDto);

        assertNotNull(saved);
        assertEquals(testNodeDto.getId(), saved.getId());
        assertEquals(testNodeDto.getContractId(), saved.getContract().getId());
        verify(contractNodeRepository).save(any(ContractNode.class));
    }

    @Test
    @DisplayName("Should find ContractNode by contract ID")
    void findByContractId_ReturnsNode() {
        when(contractNodeRepository.findByContractId(1L)).thenReturn(Optional.of(testNode));

        Optional<ContractNode> found = nodeService.findByContractId(1L);

        assertTrue(found.isPresent());
        assertEquals(testNode.getId(), found.get().getId());
        assertEquals(testContract.getId(), found.get().getContract().getId());
        verify(contractNodeRepository).findByContractId(1L);
    }

    @Test
    @DisplayName("Should find downstream contract nodes")
    void findDownstreamContractNodes_ReturnsNodes() {
        ContractNode downstreamNode = new ContractNode();
        downstreamNode.setId(2L);
        Set<ContractNode> nodes = new HashSet<>(Arrays.asList(downstreamNode));

        when(contractNodeRepository.findDownstreamContractNodes(1L)).thenReturn(nodes);

        Set<ContractNode> found = nodeService.findDownstreamContractNodes(1L);

        assertEquals(1, found.size());
        assertTrue(found.contains(downstreamNode));
        verify(contractNodeRepository).findDownstreamContractNodes(1L);
    }

    @Test
    @DisplayName("Should find upstream contract nodes")
    void findUpstreamContractNodes_ReturnsNodes() {
        ContractNode upstreamNode = new ContractNode();
        upstreamNode.setId(2L);
        Set<ContractNode> nodes = new HashSet<>(Arrays.asList(upstreamNode));

        when(contractNodeRepository.findUpstreamContractNodes(1L)).thenReturn(nodes);

        Set<ContractNode> found = nodeService.findUpstreamContractNodes(1L);

        assertEquals(1, found.size());
        assertTrue(found.contains(upstreamNode));
        verify(contractNodeRepository).findUpstreamContractNodes(1L);
    }

    @Test
    @DisplayName("Should verify existing node")
    void verify_ExistingNode_NoException() {
        when(contractNodeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> nodeService.verify(testNode));
        verify(contractNodeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing node during verification")
    void verify_NonExistingNode_ThrowsException() {
        when(contractNodeRepository.existsById(1L)).thenReturn(false);

        assertThrows(ContractNodeNotFoundException.class, () -> nodeService.verify(testNode));
        verify(contractNodeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for invalid type during verification")
    void verify_InvalidType_ThrowsException() {
        String invalidObject = "Invalid Type";

        assertThrows(TypeNotMatchException.class, () -> nodeService.verify(invalidObject));
    }

    @Test
    @DisplayName("Should check if node exists by ID")
    void existsById_ExistingId_ReturnsTrue() {
        when(contractNodeRepository.existsById(1L)).thenReturn(true);

        boolean exists = nodeService.existsById(1L);

        assertTrue(exists);
        verify(contractNodeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should find all contract nodes")
    void findAll_ReturnsAllNodes() {
        ContractNode node2 = new ContractNode();
        node2.setId(2L);
        List<ContractNode> nodes = Arrays.asList(testNode, node2);

        when(contractNodeRepository.findAll()).thenReturn(nodes);

        List<ContractNode> result = nodeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contractNodeRepository).findAll();
    }
}
