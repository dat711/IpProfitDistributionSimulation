package com.LegalEntitiesManagement.v1.Entities.repositories;

import com.LegalEntitiesManagement.v1.Entities.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
