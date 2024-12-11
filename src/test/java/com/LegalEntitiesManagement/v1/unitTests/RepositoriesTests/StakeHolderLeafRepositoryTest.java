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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StakeHolderLeafRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private StakeHolderLeafRepository stakeHolderLeafRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractNodeRepository contractNodeRepository;

    @Autowired
    private ResponsibilityRepository responsibilityRepository;

    private Role testRole;
    private StakeHolder testStakeHolder;
    private Contract testContract;
    private ContractNode testContractNode;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test stakeholder
        testStakeHolder = new StakeHolder("Test StakeHolder", testRole);
        testStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Create test contract
        testContract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testStakeHolder
        );
        testContract = contractRepository.save(testContract);

        // Create test contract node
        testContractNode = new ContractNode(testContract);
        testContractNode = contractNodeRepository.save(testContractNode);
    }

    @Test
    @Order(1)
    void testSaveStakeHolderLeaf() {
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf savedLeaf = stakeHolderLeafRepository.save(leaf);

        assertNotNull(savedLeaf.getId());
        assertEquals(testStakeHolder.getId(), savedLeaf.getStakeHolder().getId());
    }

    @Test
    @Order(2)
    void testFindByStakeholderId() {
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        stakeHolderLeafRepository.save(leaf);

        Optional<StakeHolderLeaf> found = stakeHolderLeafRepository
                .findByStakeholderId(testStakeHolder.getId());

        assertTrue(found.isPresent());
        assertEquals(testStakeHolder.getId(), found.get().getStakeHolder().getId());
    }

    @Test
    @Order(3)
    void testFindLeafNodesForContractNode() {
        // Create a leaf node
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf savedLeaf = stakeHolderLeafRepository.save(leaf);

        // Create responsibility connecting contract node to leaf
        Responsibility responsibility = new Responsibility(savedLeaf, testContractNode, 100.0);
        responsibilityRepository.save(responsibility);

        Set<StakeHolderLeaf> leafNodes = stakeHolderLeafRepository
                .findLeafNodesForContractNode(testContractNode.getId());

        assertNotNull(leafNodes);
        assertFalse(leafNodes.isEmpty());
        assertTrue(leafNodes.stream()
                .anyMatch(l -> l.getId().equals(savedLeaf.getId())));
    }

    @Test
    @Order(4)
    void testFindByRoleId() {
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        stakeHolderLeafRepository.save(leaf);

        Set<StakeHolderLeaf> leafNodes = stakeHolderLeafRepository
                .findByRoleId(testRole.getId());

        assertNotNull(leafNodes);
        assertFalse(leafNodes.isEmpty());
        assertTrue(leafNodes.stream()
                .anyMatch(l -> l.getStakeHolder().getRole().getId().equals(testRole.getId())));
    }

    @Test
    @Order(5)
    void testFindDownstreamLeafNodesWithMinPercentage() {
        // Create a leaf node
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf savedLeaf = stakeHolderLeafRepository.save(leaf);

        // Create responsibility with 50% share
        Responsibility responsibility = new Responsibility(savedLeaf, testContractNode, 50.0);
        responsibilityRepository.save(responsibility);

        // Test finding nodes with >= 40% share
        Set<StakeHolderLeaf> highShareNodes = stakeHolderLeafRepository
                .findDownstreamLeafNodesWithMinPercentage(testContractNode.getId(), 40.0);

        // Test finding nodes with >= 60% share
        Set<StakeHolderLeaf> lowShareNodes = stakeHolderLeafRepository
                .findDownstreamLeafNodesWithMinPercentage(testContractNode.getId(), 60.0);

        assertFalse(highShareNodes.isEmpty());
        assertTrue(lowShareNodes.isEmpty());
    }

    @Test
    @Order(6)
    void testFindById() {
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf savedLeaf = stakeHolderLeafRepository.save(leaf);

        Optional<StakeHolderLeaf> found = stakeHolderLeafRepository.findById(savedLeaf.getId());

        assertTrue(found.isPresent());
        assertEquals(savedLeaf.getId(), found.get().getId());
        assertEquals(testStakeHolder.getId(), found.get().getStakeHolder().getId());
    }

    @Test
    @Order(7)
    void testExistsById() {
        StakeHolderLeaf leaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf savedLeaf = stakeHolderLeafRepository.save(leaf);

        boolean exists = stakeHolderLeafRepository.existsById(savedLeaf.getId());
        boolean notExists = stakeHolderLeafRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @Order(8)
    void testFindAll() {
        // Create another stakeholder and leaf
        StakeHolder anotherStakeHolder = new StakeHolder("Another StakeHolder", testRole);
        anotherStakeHolder = stakeHolderRepository.save(anotherStakeHolder);

        StakeHolderLeaf leaf1 = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf leaf2 = new StakeHolderLeaf(anotherStakeHolder);

        stakeHolderLeafRepository.save(leaf1);
        stakeHolderLeafRepository.save(leaf2);

        var allLeaves = stakeHolderLeafRepository.findAll();

        assertNotNull(allLeaves);
        assertTrue(allLeaves.size() >= 2);
    }

    @Test
    @Order(9)
    void testFindByStakeholderIds() {
        // Create additional stakeholders
        StakeHolder secondStakeHolder = new StakeHolder("Second StakeHolder", testRole);
        StakeHolder thirdStakeHolder = new StakeHolder("Third StakeHolder", testRole);
        secondStakeHolder = stakeHolderRepository.save(secondStakeHolder);
        thirdStakeHolder = stakeHolderRepository.save(thirdStakeHolder);

        // Create and save leaves in a different order
        StakeHolderLeaf thirdLeaf = new StakeHolderLeaf(thirdStakeHolder);
        StakeHolderLeaf firstLeaf = new StakeHolderLeaf(testStakeHolder);
        StakeHolderLeaf secondLeaf = new StakeHolderLeaf(secondStakeHolder);

        stakeHolderLeafRepository.save(thirdLeaf);
        stakeHolderLeafRepository.save(firstLeaf);
        stakeHolderLeafRepository.save(secondLeaf);

        // Create list of IDs in specific order
        List<Long> stakeholderIds = Arrays.asList(
                testStakeHolder.getId(),
                secondStakeHolder.getId(),
                thirdStakeHolder.getId()
        );

        // Get leaves using new method
        List<StakeHolderLeaf> orderedLeaves = stakeHolderLeafRepository
                .findByStakeholderIds(stakeholderIds);

        // Verify results
        assertNotNull(orderedLeaves);
        assertEquals(3, orderedLeaves.size());

        // Verify each leaf corresponds to correct stakeholder in order
        Set<StakeHolder> expectedStakeHolders = Set.of(testStakeHolder, secondStakeHolder, thirdStakeHolder);
        Set<StakeHolder> actualStakeHolders = orderedLeaves.stream()
                .map(StakeHolderLeaf::getStakeHolder)
                .collect(Collectors.toSet());

        assertTrue(actualStakeHolders.containsAll(expectedStakeHolders));
    }
}
