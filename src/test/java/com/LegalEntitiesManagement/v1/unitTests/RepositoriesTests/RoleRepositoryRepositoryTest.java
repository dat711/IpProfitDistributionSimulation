package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;
import com.LegalEntitiesManagement.v1.Entities.exceptions.RoleNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleRepositoryRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Order(1)
    void TestRoleInsertingFunction(){
        Role taxCollector = new Role("Nation Tax collector", "They collect the income tax",  3);
        Role savedRole = this.roleRepository.save(taxCollector);
        assertTrue(savedRole.getId() > 0);
    }

    @Test
    @Order(2)
    void TestRoleExistFunction(){
        Role taxCollector = new Role("Nation Tax collector", "They collect the income tax", 3);
        Role savedRole = this.roleRepository.save(taxCollector);
        assertTrue(this.roleRepository.existsById(2L));
    }

    @Test
    @Order(3)
    void TestGetRoleFunction(){
        String name = "Nation Tax collector";
        String description = "They collect the income tax";
        Role taxCollector = new Role(name, description, 3);
        this.roleRepository.save(taxCollector);
        Role getRole = this.roleRepository.findById(3L).orElseThrow(() -> new RoleNotFoundException(3L));
        assertEquals(getRole.getName(), name);
        assertEquals(getRole.getDescription(), description);
        assertEquals(getRole.getPriority(), 3);
    }

}
