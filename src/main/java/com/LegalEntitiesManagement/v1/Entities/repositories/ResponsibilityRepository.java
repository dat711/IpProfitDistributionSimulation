package com.LegalEntitiesManagement.v1.Entities.repositories;

import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ResponsibilityRepository extends JpaRepository<Responsibility, Long>{
    @Query("SELECT r FROM Responsibility r WHERE r.source.id = :sourceId AND r.target.id = :targetId")
    Optional<Responsibility> findBySourceAndTarget(
            @Param("sourceId") Long sourceId,
            @Param("targetId") Long targetId);

    @Query("SELECT r FROM Responsibility r WHERE r.source.id = :nodeId")
    Set<Responsibility> findDownstreamEdges(@Param("nodeId") Long nodeId);

    @Query("SELECT r FROM Responsibility r WHERE r.target.id = :nodeId")
    Set<Responsibility> findUpstreamEdges(@Param("nodeId") Long nodeId);

    @Query("DELETE FROM Responsibility r WHERE r IN :responsibilities")
    @Modifying
    @Transactional
    void deleteAllBatch(@Param("responsibilities") Collection<Responsibility> responsibilities);

    @Query("SELECT r FROM Responsibility r WHERE r.target.id IN :nodeIds")
    Set<Responsibility> findUpstreamEdgesByNodeIds(@Param("nodeIds") Collection<Long> nodeIds);
}
