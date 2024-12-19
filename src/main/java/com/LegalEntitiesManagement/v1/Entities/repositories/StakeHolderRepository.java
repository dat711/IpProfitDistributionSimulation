package com.LegalEntitiesManagement.v1.Entities.repositories;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StakeHolderRepository extends JpaRepository<StakeHolder, Long>{
    @Query("SELECT sh FROM StakeHolder sh WHERE sh.role.id = :roleId")
    List<StakeHolder> findByRoleId(@Param("roleId") Long roleId);

}
