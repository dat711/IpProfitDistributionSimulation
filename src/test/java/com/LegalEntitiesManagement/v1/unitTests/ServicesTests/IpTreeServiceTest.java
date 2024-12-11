package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;
import com.LegalEntitiesManagement.v1.Entities.dto.IpTreeDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpTreeNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpTreeRepository;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.IpTreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class IpTreeServiceTest extends BaseServiceTestMockedDependencies {
    private IpTreeService ipTreeService;
    private IpTree testIpTree;
    private IpTreeDto testIpTreeDto;
    private IntellectualProperty testIp;
    private ContractNode testRootContractNode;
    private Contract testContract;
    private StakeHolder testExecutor;
    private Role testRole;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        ipTreeService = new IpTreeService(ipTreeRepository);

        // Create test data hierarchy
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole.setId(1L);

        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor.setId(1L);

        testContract = new Contract("Test Contract", LocalDate.now(), 1, testExecutor);
        testContract.setId(1L);

        testIp = new IntellectualProperty("Test IP", "Test IP Description");
        testIp.setId(1L);

        testRootContractNode = new ContractNode(testContract);
        testRootContractNode.setId(1L);

        testIpTree = new IpTree(testIp, testRootContractNode);
        testIpTree.setId(1L);

        testIpTreeDto = new IpTreeDto(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should find IpTree by ID")
    void findById_ExistingId_ReturnsIpTree() {
        when(ipTreeRepository.findById(1L)).thenReturn(Optional.of(testIpTree));

        IpTree found = ipTreeService.findById(1L);

        assertNotNull(found);
        assertEquals(testIpTree.getId(), found.getId());
        assertEquals(testIp.getId(), found.getIntellectualProperty().getId());
        assertEquals(testRootContractNode.getId(), found.getRootContractNode().getId());
        verify(ipTreeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when IpTree not found")
    void findById_NonExistingId_ThrowsException() {
        when(ipTreeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IpTreeNotFoundException.class, () -> ipTreeService.findById(99L));
        verify(ipTreeRepository).findById(99L);
    }

    @Test
    @DisplayName("Should save IpTree")
    void save_ValidIpTree_ReturnsSavedIpTree() {
        when(ipTreeRepository.save(any(IpTree.class))).thenReturn(testIpTree);

        IpTree saved = ipTreeService.save(testIpTree);

        assertNotNull(saved);
        assertEquals(testIpTree.getId(), saved.getId());
        assertEquals(testIp.getId(), saved.getIntellectualProperty().getId());
        assertEquals(testRootContractNode.getId(), saved.getRootContractNode().getId());
        verify(ipTreeRepository).save(testIpTree);
    }

    @Test
    @DisplayName("Should save IpTree from DTO")
    void saveFromDto_ValidDto_ReturnsSavedIpTree() {
        when(ipTreeRepository.save(any(IpTree.class))).thenReturn(testIpTree);

        IpTree saved = ipTreeService.saveFromDto(testIpTreeDto);

        assertNotNull(saved);
        assertEquals(testIpTreeDto.getId(), saved.getId());
        assertEquals(testIpTreeDto.getIntellectualPropertyId(), saved.getIntellectualProperty().getId());
        assertEquals(testIpTreeDto.getRootContractNodeId(), saved.getRootContractNode().getId());
        verify(ipTreeRepository).save(any(IpTree.class));
    }

    @Test
    @DisplayName("Should find IpTree by IntellectualProperty ID")
    void findByIntellectualPropertyId_ReturnsIpTree() {
        when(ipTreeRepository.findByIntellectualPropertyId(1L)).thenReturn(Optional.of(testIpTree));

        Optional<IpTree> found = ipTreeService.findByIntellectualPropertyId(1L);

        assertTrue(found.isPresent());
        assertEquals(testIpTree.getId(), found.get().getId());
        assertEquals(testIp.getId(), found.get().getIntellectualProperty().getId());
        verify(ipTreeRepository).findByIntellectualPropertyId(1L);
    }

    @Test
    @DisplayName("Should verify existing IpTree")
    void verify_ExistingIpTree_NoException() {
        when(ipTreeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> ipTreeService.verify(testIpTree));
        verify(ipTreeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existing IpTree during verification")
    void verify_NonExistingIpTree_ThrowsException() {
        when(ipTreeRepository.existsById(1L)).thenReturn(false);

        assertThrows(IpTreeNotFoundException.class, () -> ipTreeService.verify(testIpTree));
        verify(ipTreeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception for invalid type during verification")
    void verify_InvalidType_ThrowsException() {
        String invalidObject = "Invalid Type";

        assertThrows(TypeNotMatchException.class, () -> ipTreeService.verify(invalidObject));
    }

    @Test
    @DisplayName("Should find all IpTrees")
    void findAll_ReturnsAllIpTrees() {
        IpTree ipTree2 = new IpTree();
        ipTree2.setId(2L);
        List<IpTree> ipTrees = Arrays.asList(testIpTree, ipTree2);

        when(ipTreeRepository.findAll()).thenReturn(ipTrees);

        List<IpTree> result = ipTreeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ipTreeRepository).findAll();
    }
}
