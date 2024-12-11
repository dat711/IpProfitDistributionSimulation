package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ResponsibilityRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private ResponsibilityRepository responsibilityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractNodeRepository contractNodeRepository;

    @Autowired
    private StakeHolderLeafRepository stakeHolderLeafRepository;

    private Role testRole;
    private StakeHolder testExecutor;
    private Contract testContract;
    private ContractNode sourceNode;
    private ContractNode targetNode;
    private StakeHolderLeaf leafNode;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);

        // Create test contracts
        testContract = new Contract(
                "Source Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        testContract = contractRepository.save(testContract);

        Contract targetContract = new Contract(
                "Target Contract",
                LocalDate.now(),
                2,
                testExecutor
        );
        targetContract = contractRepository.save(targetContract);

        // Create source and target nodes
        sourceNode = new ContractNode(testContract);
        sourceNode = contractNodeRepository.save(sourceNode);

        targetNode = new ContractNode(targetContract);
        targetNode = contractNodeRepository.save(targetNode);

        // Create leaf node
        leafNode = new StakeHolderLeaf(testExecutor);
        leafNode = stakeHolderLeafRepository.save(leafNode);
    }

    @Test
    @Order(1)
    void testSaveResponsibility() {
        Responsibility responsibility = new Responsibility(targetNode, sourceNode, 50.0);
        Responsibility savedResponsibility = responsibilityRepository.save(responsibility);

        assertNotNull(savedResponsibility.getId());
        assertEquals(sourceNode.getId(), savedResponsibility.getSource().getId());
        assertEquals(targetNode.getId(), savedResponsibility.getTarget().getId());
        assertEquals(50.0, savedResponsibility.getPercentage());
    }

    @Test
    @Order(2)
    void testFindBySourceAndTarget() {
        Responsibility responsibility = new Responsibility(targetNode, sourceNode, 50.0);
        responsibilityRepository.save(responsibility);

        Optional<Responsibility> found = responsibilityRepository
                .findBySourceAndTarget(sourceNode.getId(), targetNode.getId());

        assertTrue(found.isPresent());
        assertEquals(sourceNode.getId(), found.get().getSource().getId());
        assertEquals(targetNode.getId(), found.get().getTarget().getId());
    }

    @Test
    @Order(3)
    void testFindDownstreamEdges() {
        // Create two downstream edges
        Responsibility responsibility1 = new Responsibility(targetNode, sourceNode, 30.0);
        Responsibility responsibility2 = new Responsibility(leafNode, sourceNode, 70.0);

        responsibilityRepository.save(responsibility1);
        responsibilityRepository.save(responsibility2);

        Set<Responsibility> downstreamEdges = responsibilityRepository
                .findDownstreamEdges(sourceNode.getId());

        assertNotNull(downstreamEdges);
        assertEquals(2, downstreamEdges.size());
        assertTrue(downstreamEdges.stream()
                .allMatch(r -> r.getSource().getId().equals(sourceNode.getId())));
    }

    @Test
    @Order(4)
    void testFindUpstreamEdges() {
        // Create two edges targeting the same node
        Responsibility responsibility1 = new Responsibility(targetNode, sourceNode, 100.0);
        responsibilityRepository.save(responsibility1);

        Set<Responsibility> upstreamEdges = responsibilityRepository
                .findUpstreamEdges(targetNode.getId());

        assertNotNull(upstreamEdges);
        assertFalse(upstreamEdges.isEmpty());
        assertTrue(upstreamEdges.stream()
                .allMatch(r -> r.getTarget().getId().equals(targetNode.getId())));
    }

    @Test
    @Order(5)
    void testFindById() {
        Responsibility responsibility = new Responsibility(targetNode, sourceNode, 50.0);
        Responsibility savedResponsibility = responsibilityRepository.save(responsibility);

        Optional<Responsibility> found = responsibilityRepository.findById(savedResponsibility.getId());

        assertTrue(found.isPresent());
        assertEquals(savedResponsibility.getId(), found.get().getId());
        assertEquals(sourceNode.getId(), found.get().getSource().getId());
        assertEquals(targetNode.getId(), found.get().getTarget().getId());
    }

    @Test
    @Order(6)
    void testExistsById() {
        Responsibility responsibility = new Responsibility(targetNode, sourceNode, 50.0);
        Responsibility savedResponsibility = responsibilityRepository.save(responsibility);

        boolean exists = responsibilityRepository.existsById(savedResponsibility.getId());
        boolean notExists = responsibilityRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @Order(7)
    void testFindAll() {
        // Create multiple responsibilities
        Responsibility responsibility1 = new Responsibility(targetNode, sourceNode, 30.0);
        Responsibility responsibility2 = new Responsibility(leafNode, sourceNode, 70.0);

        responsibilityRepository.save(responsibility1);
        responsibilityRepository.save(responsibility2);

        var allResponsibilities = responsibilityRepository.findAll();

        assertNotNull(allResponsibilities);
        assertTrue(allResponsibilities.size() >= 2);
    }

    @Test
    @Order(8)
    void testSaveResponsibilityWithStakeHolderLeaf() {
        Responsibility responsibility = new Responsibility(leafNode, sourceNode, 100.0);
        Responsibility savedResponsibility = responsibilityRepository.save(responsibility);

        assertNotNull(savedResponsibility.getId());
        assertEquals(sourceNode.getId(), savedResponsibility.getSource().getId());
        assertEquals(leafNode.getId(), savedResponsibility.getTarget().getId());
        assertEquals(100.0, savedResponsibility.getPercentage());
    }

    @Test
    @Order(9)
    void testValidPercentageRange() {
        // Test with valid percentage
        Responsibility validResponsibility = new Responsibility(targetNode, sourceNode, 50.0);
        Responsibility savedValid = responsibilityRepository.save(validResponsibility);
        assertEquals(50.0, savedValid.getPercentage());

        // Test with boundary values
        Responsibility minResponsibility = new Responsibility(targetNode, sourceNode, 0.0);
        Responsibility maxResponsibility = new Responsibility(targetNode, sourceNode, 100.0);

        Responsibility savedMin = responsibilityRepository.save(minResponsibility);
        Responsibility savedMax = responsibilityRepository.save(maxResponsibility);

        assertEquals(0.0, savedMin.getPercentage());
        assertEquals(100.0, savedMax.getPercentage());
    }

    @Test
    @Order(10)
    void testBatchDelete() {
        // Create multiple responsibilities to delete
        Responsibility responsibility1 = new Responsibility(targetNode, sourceNode, 30.0);
        Responsibility responsibility2 = new Responsibility(leafNode, sourceNode, 70.0);

        // Save the responsibilities
        responsibility1 = responsibilityRepository.save(responsibility1);
        responsibility2 = responsibilityRepository.save(responsibility2);

        // Create a collection of responsibilities to delete
        List<Responsibility> toDelete = Arrays.asList(responsibility1, responsibility2);

        // Verify responsibilities exist before deletion
        assertTrue(responsibilityRepository.existsById(responsibility1.getId()));
        assertTrue(responsibilityRepository.existsById(responsibility2.getId()));

        // Perform batch delete
        responsibilityRepository.deleteAllBatch(toDelete);

        // Verify responsibilities were deleted
        assertFalse(responsibilityRepository.existsById(responsibility1.getId()));
        assertFalse(responsibilityRepository.existsById(responsibility2.getId()));
    }

    @Test
    @Order(11)
    void testFindUpstreamEdgesByNodeIds() {
        // Create additional target node for testing multiple targets
        Contract additionalContract = new Contract(
                "Additional Target Contract",
                LocalDate.now(),
                3,
                testExecutor
        );
        additionalContract = contractRepository.save(additionalContract);

        ContractNode additionalTargetNode = new ContractNode(additionalContract);
        additionalTargetNode = contractNodeRepository.save(additionalTargetNode);

        // Create multiple responsibilities
        Responsibility responsibility1 = new Responsibility(targetNode, sourceNode, 30.0);
        Responsibility responsibility2 = new Responsibility(additionalTargetNode, sourceNode, 70.0);

        responsibility1 = responsibilityRepository.save(responsibility1);
        responsibility2 = responsibilityRepository.save(responsibility2);

        // Test finding upstream edges for multiple nodes
        Set<Responsibility> results = responsibilityRepository.findUpstreamEdgesByNodeIds(
                Arrays.asList(targetNode.getId(), additionalTargetNode.getId())
        );

        // Verify results
        assertNotNull(results);
        assertEquals(2, results.size());

        // Convert to map for easier verification
        Map<Long, Double> percentagesByTargetId = results.stream()
                .collect(Collectors.toMap(
                        r -> r.getTarget().getId(),
                        Responsibility::getPercentage
                ));

        // Verify each target node has correct upstream edge
        assertTrue(percentagesByTargetId.containsKey(targetNode.getId()));
        assertTrue(percentagesByTargetId.containsKey(additionalTargetNode.getId()));

        // Verify edge properties
        assertEquals(30.0, percentagesByTargetId.get(targetNode.getId()));
        assertEquals(70.0, percentagesByTargetId.get(additionalTargetNode.getId()));

        // Verify all edges have the correct source
        assertTrue(results.stream()
                .allMatch(r -> r.getSource().getId().equals(sourceNode.getId())));
    }

    @Test
    @Order(12)
    void testFindUpstreamEdgesByNodeIds_WithNonExistentNodes() {
        // Create and save a test responsibility
        Responsibility responsibility = new Responsibility(targetNode, sourceNode, 50.0);
        responsibilityRepository.save(responsibility);

        // Test with mix of existing and non-existent node IDs
        Set<Responsibility> results = responsibilityRepository.findUpstreamEdgesByNodeIds(
                Arrays.asList(targetNode.getId(), 999L)
        );

        // Verify results
        assertNotNull(results);
        assertEquals(1, results.size());

        // Verify the correct responsibility was returned
        Responsibility foundResponsibility = results.iterator().next();
        assertEquals(50.0, foundResponsibility.getPercentage());
        assertEquals(sourceNode.getId(), foundResponsibility.getSource().getId());
        assertEquals(targetNode.getId(), foundResponsibility.getTarget().getId());
    }

    @Test
    @Order(13)
    void testFindUpstreamEdgesByNodeIds_WithEmptyList() {
        Set<Responsibility> results = responsibilityRepository.findUpstreamEdgesByNodeIds(Collections.emptyList());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
