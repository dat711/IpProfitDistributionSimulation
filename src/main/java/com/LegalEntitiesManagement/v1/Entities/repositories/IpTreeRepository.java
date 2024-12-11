package com.LegalEntitiesManagement.v1.Entities.repositories;

import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface IpTreeRepository extends JpaRepository<IpTree, Long> {
    @Query("SELECT it FROM IpTree it WHERE it.intellectualProperty.id = :ipId")
    Optional<IpTree> findByIntellectualPropertyId(@Param("ipId") Long ipId);
}
