package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.Entities.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IpTreeRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private IpTreeRepository ipTreeRepository;

    @Autowired
    private IntellectualPropertyRepository intellectualPropertyRepository;

    @Autowired
    private ContractNodeRepository contractNodeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    private IntellectualProperty testIp;
    private ContractNode rootContractNode;
    private Role testRole;
    private StakeHolder testExecutor;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test executor
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);

        // Create test IP
        testIp = new IntellectualProperty("Test IP", "Test IP Description");
        testIp = intellectualPropertyRepository.save(testIp);

        // Create test contract
        Contract rootContract = new Contract(
                "Root Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        rootContract = contractRepository.save(rootContract);

        // Create root contract node
        rootContractNode = new ContractNode(rootContract);
        rootContractNode = contractNodeRepository.save(rootContractNode);
    }

    @Test
    @Order(1)
    void testSaveIpTree() {
        IpTree ipTree = new IpTree(testIp, rootContractNode);
        IpTree savedIpTree = ipTreeRepository.save(ipTree);

        assertNotNull(savedIpTree.getId());
        assertEquals(testIp.getId(), savedIpTree.getIntellectualProperty().getId());
        assertEquals(rootContractNode.getId(), savedIpTree.getRootContractNode().getId());
    }

    @Test
    @Order(2)
    void testFindByIntellectualPropertyId() {
        IpTree ipTree = new IpTree(testIp, rootContractNode);
        ipTreeRepository.save(ipTree);

        Optional<IpTree> found = ipTreeRepository.findByIntellectualPropertyId(testIp.getId());

        assertTrue(found.isPresent());
        assertEquals(testIp.getId(), found.get().getIntellectualProperty().getId());
        assertEquals(rootContractNode.getId(), found.get().getRootContractNode().getId());
    }

    @Test
    @Order(3)
    void testFindById() {
        IpTree ipTree = new IpTree(testIp, rootContractNode);
        IpTree savedIpTree = ipTreeRepository.save(ipTree);

        Optional<IpTree> found = ipTreeRepository.findById(savedIpTree.getId());

        assertTrue(found.isPresent());
        assertEquals(savedIpTree.getId(), found.get().getId());
        assertEquals(testIp.getId(), found.get().getIntellectualProperty().getId());
        assertEquals(rootContractNode.getId(), found.get().getRootContractNode().getId());
    }

    @Test
    @Order(4)
    void testExistsById() {
        IpTree ipTree = new IpTree(testIp, rootContractNode);
        IpTree savedIpTree = ipTreeRepository.save(ipTree);

        boolean exists = ipTreeRepository.existsById(savedIpTree.getId());
        boolean notExists = ipTreeRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @Order(5)
    void testFindAll() {
        // Create second IP and contract node
        IntellectualProperty secondIp = new IntellectualProperty("Second IP", "Second IP Description");
        secondIp = intellectualPropertyRepository.save(secondIp);

        Contract secondContract = new Contract(
                "Second Contract",
                LocalDate.now(),
                2,
                testExecutor
        );
        secondContract = contractRepository.save(secondContract);
        ContractNode secondNode = new ContractNode(secondContract);
        secondNode = contractNodeRepository.save(secondNode);

        // Save two IP trees
        IpTree ipTree1 = new IpTree(testIp, rootContractNode);
        IpTree ipTree2 = new IpTree(secondIp, secondNode);

        ipTreeRepository.save(ipTree1);
        ipTreeRepository.save(ipTree2);

        List<IpTree> allTrees = ipTreeRepository.findAll();

        assertNotNull(allTrees);
        assertTrue(allTrees.size() >= 2);
        assertTrue(allTrees.stream()
                .anyMatch(tree -> tree.getIntellectualProperty().getId().equals(testIp.getId())));
        IntellectualProperty finalSecondIp = secondIp;
        assertTrue(allTrees.stream()
                .anyMatch(tree -> tree.getIntellectualProperty().getId().equals(finalSecondIp.getId())));
    }



    @Test
    @Order(6)
    void testCascadeOperations() {
        IpTree ipTree = new IpTree(testIp, rootContractNode);
        IpTree savedIpTree = ipTreeRepository.save(ipTree);

        // Verify the tree was saved with all relationships intact
        IpTree retrievedTree = ipTreeRepository.findById(savedIpTree.getId()).orElseThrow();

        assertNotNull(retrievedTree.getIntellectualProperty());
        assertNotNull(retrievedTree.getRootContractNode());
        assertEquals(testIp.getId(), retrievedTree.getIntellectualProperty().getId());
        assertEquals(rootContractNode.getId(), retrievedTree.getRootContractNode().getId());
    }
}
