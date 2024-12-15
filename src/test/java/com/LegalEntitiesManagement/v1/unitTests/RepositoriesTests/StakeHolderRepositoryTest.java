package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;

import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class StakeHolderRepositoryTest extends BaseRepositoryTestProperties  {
    @Autowired
    private StakeHolderRepository stakeHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;
    private StakeHolder testStakeHolder;

    @BeforeEach
    void setUp() {
        // Create and save test role
        testRole = new Role("Test Role", "Test Role Description", 1);
        testRole = roleRepository.save(testRole);

        // Create test stakeholder
        testStakeHolder = new StakeHolder("Test StakeHolder", testRole);
    }

    @Test
    @Order(1)
    void testStakeHolderInsertingFunction() {
        // Save the stakeholder
        StakeHolder savedStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Verify
        assertNotNull(savedStakeHolder.getId());
        assertEquals("Test StakeHolder", savedStakeHolder.getName());
        assertEquals(testRole.getId(), savedStakeHolder.getRole().getId());
    }

    @Test
    @Order(2)
    void testStakeHolderExistFunction() {
        // Save the stakeholder
        StakeHolder savedStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Verify existence
        assertTrue(stakeHolderRepository.existsById(savedStakeHolder.getId()));
    }

    @Test
    @Order(3)
    void testStakeHolderGetFunction() {
        // Save the stakeholder
        StakeHolder savedStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Retrieve and verify
        StakeHolder retrievedStakeHolder = stakeHolderRepository
                .findById(savedStakeHolder.getId())
                .orElseThrow(() -> new StakeHolderNotFoundException(savedStakeHolder.getId()));

        assertEquals(savedStakeHolder.getId(), retrievedStakeHolder.getId());
        assertEquals("Test StakeHolder", retrievedStakeHolder.getName());
        assertEquals(testRole.getId(), retrievedStakeHolder.getRole().getId());
    }

    @Test
    @Order(4)
    void testStakeHolderNotFound() {
        // Test for non-existent ID
        Long nonExistentId = 999L;
        assertFalse(stakeHolderRepository.existsById(nonExistentId));
        assertTrue(stakeHolderRepository.findById(nonExistentId).isEmpty());
    }

    @Test
    @Order(5)
    void testStakeHolderWithDifferentRoles() {
        // Create another role
        Role anotherRole = new Role("Another Role", "Another Role Description", 2);
        anotherRole = roleRepository.save(anotherRole);

        // Create and save stakeholder with original role
        StakeHolder stakeholder1 = new StakeHolder("StakeHolder 1", testRole);
        StakeHolder savedStakeholder1 = stakeHolderRepository.save(stakeholder1);

        // Create and save stakeholder with new role
        StakeHolder stakeholder2 = new StakeHolder("StakeHolder 2", anotherRole);
        StakeHolder savedStakeholder2 = stakeHolderRepository.save(stakeholder2);

        // Verify both stakeholders
        assertNotNull(savedStakeholder1.getId());
        assertNotNull(savedStakeholder2.getId());
        assertNotEquals(savedStakeholder1.getId(), savedStakeholder2.getId());
        assertEquals(testRole.getId(), savedStakeholder1.getRole().getId());
        assertEquals(anotherRole.getId(), savedStakeholder2.getRole().getId());
    }

    @Test
    @Order(6)
    void testUpdateStakeHolder() {
        // Save initial stakeholder
        StakeHolder savedStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Create new role for update
        Role newRole = new Role("New Role", "New Role Description", 3);
        newRole = roleRepository.save(newRole);

        // Update stakeholder
        savedStakeHolder.setName("Updated Name");
        savedStakeHolder.setRole(newRole);
        StakeHolder updatedStakeHolder = stakeHolderRepository.save(savedStakeHolder);

        // Verify update
        assertEquals(savedStakeHolder.getId(), updatedStakeHolder.getId());
        assertEquals("Updated Name", updatedStakeHolder.getName());
        assertEquals(newRole.getId(), updatedStakeHolder.getRole().getId());
    }

    @Test
    @Order(7)
    void testStakeHolderRoleAssociation() {
        // Save stakeholder
        StakeHolder savedStakeHolder = stakeHolderRepository.save(testStakeHolder);

        // Retrieve stakeholder and verify role association
        StakeHolder retrievedStakeHolder = stakeHolderRepository
                .findById(savedStakeHolder.getId())
                .orElseThrow(() -> new StakeHolderNotFoundException(savedStakeHolder.getId()));

        assertNotNull(retrievedStakeHolder.getRole());
        assertEquals(testRole.getId(), retrievedStakeHolder.getRole().getId());
        assertEquals(testRole.getName(), retrievedStakeHolder.getRole().getName());
        assertEquals(testRole.getDescription(), retrievedStakeHolder.getRole().getDescription());
        assertEquals(testRole.getPriority(), retrievedStakeHolder.getRole().getPriority());
    }

    @Test
    @Order(8)
    void testFindByRoleId() {
        // Create and save multiple stakeholders with the same role
        StakeHolder stakeholder1 = new StakeHolder("StakeHolder 1", testRole);
        StakeHolder stakeholder2 = new StakeHolder("StakeHolder 2", testRole);
        StakeHolder stakeholder3 = new StakeHolder("StakeHolder 3", testRole);

        stakeHolderRepository.save(stakeholder1);
        stakeHolderRepository.save(stakeholder2);
        stakeHolderRepository.save(stakeholder3);

        // Create another role and stakeholder
        Role anotherRole = new Role("Another Role", "Another Role Description", 2);
        anotherRole = roleRepository.save(anotherRole);

        StakeHolder stakeholder4 = new StakeHolder("StakeHolder 4", anotherRole);
        stakeHolderRepository.save(stakeholder4);

        // Test findByRoleId for testRole
        List<StakeHolder> stakeholdersForTestRole = stakeHolderRepository.findByRoleId(testRole.getId());
        assertEquals(3, stakeholdersForTestRole.size());
        assertTrue(stakeholdersForTestRole.stream().allMatch(sh -> sh.getRole().getId().equals(testRole.getId())));
        assertTrue(stakeholdersForTestRole.stream()
                .map(StakeHolder::getName)
                .allMatch(name -> name.startsWith("StakeHolder")));

        // Test findByRoleId for anotherRole
        List<StakeHolder> stakeholdersForAnotherRole = stakeHolderRepository.findByRoleId(anotherRole.getId());
        assertEquals(1, stakeholdersForAnotherRole.size());
        assertEquals("StakeHolder 4", stakeholdersForAnotherRole.get(0).getName());
        assertEquals(anotherRole.getId(), stakeholdersForAnotherRole.get(0).getRole().getId());

        // Test findByRoleId for non-existent role
        List<StakeHolder> stakeholdersForNonExistentRole = stakeHolderRepository.findByRoleId(999L);
        assertTrue(stakeholdersForNonExistentRole.isEmpty());
    }
}
