package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractNodeRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ContractNodeRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private ContractNodeRepository contractNodeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    private Role testRole;
    private StakeHolder testExecutor;

    @BeforeEach
    void setUp() {
        // Create a test role if it doesn't exist
        testRole = new Role("Test Role", "Test Description", 1);
        testRole = roleRepository.save(testRole);

        // Create a test executor if it doesn't exist
        testExecutor = new StakeHolder("Test Executor", testRole);
        testExecutor = stakeHolderRepository.save(testExecutor);
    }

    @Test
    @Order(1)
    void testContractNodeInsertion() {
        // Create and save a test contract first
        Contract contract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedContract = contractRepository.save(contract);

        // Create and save a contract node
        ContractNode node = new ContractNode(savedContract);
        ContractNode savedNode = contractNodeRepository.save(node);

        // Verify
        assertNotNull(savedNode.getId());
        assertEquals(savedContract.getId(), savedNode.getContract().getId());
    }

    @Test
    @Order(2)
    void testFindByContractId() {
        // Create and save a contract first
        Contract contract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedContract = contractRepository.save(contract);

        // Create and save a contract node
        ContractNode node = new ContractNode(savedContract);
        contractNodeRepository.save(node);

        // Test finding by contract ID
        Optional<ContractNode> found = contractNodeRepository.findByContractId(savedContract.getId());

        assertTrue(found.isPresent());
        assertEquals(savedContract.getId(), found.get().getContract().getId());
    }

    @Test
    @Order(3)
    void testFindDownstreamContractNodes() {
        // Create and save source contract
        Contract sourceContract = new Contract(
                "Source Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedSourceContract = contractRepository.save(sourceContract);

        // Create and save target contract
        Contract targetContract = new Contract(
                "Target Contract",
                LocalDate.now(),
                2,
                testExecutor
        );
        Contract savedTargetContract = contractRepository.save(targetContract);

        // Create and save source node
        ContractNode sourceNode = new ContractNode(savedSourceContract);
        ContractNode savedSourceNode = contractNodeRepository.save(sourceNode);

        // Create and save target node
        ContractNode targetNode = new ContractNode(savedTargetContract);
        ContractNode savedTargetNode = contractNodeRepository.save(targetNode);

        // Create responsibility/edge connecting them
        Responsibility responsibility = new Responsibility(savedTargetNode, savedSourceNode, 100.0);
        savedSourceNode.getDownStreamEdges().add(responsibility);
        savedTargetNode.getUpStreamEdges().add(responsibility);

        contractNodeRepository.save(savedSourceNode);
        contractNodeRepository.save(savedTargetNode);

        // Test finding downstream nodes
        Set<ContractNode> downstream = contractNodeRepository.findDownstreamContractNodes(savedSourceNode.getId());

        assertNotNull(downstream);
        assertFalse(downstream.isEmpty());
        assertTrue(downstream.stream().anyMatch(node -> node.getId().equals(savedTargetNode.getId())));
    }

    @Test
    @Order(4)
    void testFindUpstreamContractNodes() {
        // Create and save source contract
        Contract sourceContract = new Contract(
                "Source Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedSourceContract = contractRepository.save(sourceContract);

        // Create and save target contract
        Contract targetContract = new Contract(
                "Target Contract",
                LocalDate.now(),
                2,
                testExecutor
        );
        Contract savedTargetContract = contractRepository.save(targetContract);

        // Create and save source node
        ContractNode sourceNode = new ContractNode(savedSourceContract);
        ContractNode savedSourceNode = contractNodeRepository.save(sourceNode);

        // Create and save target node
        ContractNode targetNode = new ContractNode(savedTargetContract);
        ContractNode savedTargetNode = contractNodeRepository.save(targetNode);

        // Create responsibility/edge connecting them
        Responsibility responsibility = new Responsibility(savedTargetNode, savedSourceNode, 100.0);
        savedSourceNode.getDownStreamEdges().add(responsibility);
        savedTargetNode.getUpStreamEdges().add(responsibility);

        contractNodeRepository.save(savedSourceNode);
        contractNodeRepository.save(savedTargetNode);

        // Test finding upstream nodes
        Set<ContractNode> upstream = contractNodeRepository.findUpstreamContractNodes(savedTargetNode.getId());

        assertNotNull(upstream);
        assertFalse(upstream.isEmpty());
        assertTrue(upstream.stream().anyMatch(node -> node.getId().equals(savedSourceNode.getId())));
    }

    @Test
    @Order(5)
    void testContractNodeExists() {
        // Create and save a contract first
        Contract contract = new Contract(
                "Test Contract",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedContract = contractRepository.save(contract);

        // Create and save a contract node
        ContractNode node = new ContractNode(savedContract);
        ContractNode savedNode = contractNodeRepository.save(node);

        // Test exists by id
        assertTrue(contractNodeRepository.existsById(savedNode.getId()));
        assertFalse(contractNodeRepository.existsById(999L));
    }

    @Test
    @Order(6)
    void testFindAll() {
        // Create and save first contract
        Contract contract1 = new Contract(
                "Test Contract 1",
                LocalDate.now(),
                1,
                testExecutor
        );
        Contract savedContract1 = contractRepository.save(contract1);

        // Create and save second contract
        Contract contract2 = new Contract(
                "Test Contract 2",
                LocalDate.now(),
                2,
                testExecutor
        );
        Contract savedContract2 = contractRepository.save(contract2);

        // Create and save contract nodes
        ContractNode node1 = new ContractNode(savedContract1);
        ContractNode node2 = new ContractNode(savedContract2);

        contractNodeRepository.save(node1);
        contractNodeRepository.save(node2);

        // Test finding all
        var allNodes = contractNodeRepository.findAll();

        assertNotNull(allNodes);
        assertTrue(allNodes.size() >= 2);
    }


}
