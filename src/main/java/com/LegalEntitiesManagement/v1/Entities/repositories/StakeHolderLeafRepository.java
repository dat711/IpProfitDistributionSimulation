package com.LegalEntitiesManagement.v1.Entities.repositories;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
public interface StakeHolderLeafRepository extends JpaRepository<StakeHolderLeaf, Long> {
    @Query("SELECT sl FROM StakeHolderLeaf sl WHERE sl.stakeHolder.id = :stakeholderId")
    Optional<StakeHolderLeaf> findByStakeholderId(@Param("stakeholderId") Long stakeholderId);

    @Query("SELECT sl FROM StakeHolderLeaf sl WHERE sl.id IN " +
            "(SELECT r.target.id FROM Responsibility r WHERE r.source.id = :contractNodeId)")
    Set<StakeHolderLeaf> findLeafNodesForContractNode(@Param("contractNodeId") Long contractNodeId);

    @Query("SELECT sl FROM StakeHolderLeaf sl WHERE sl.stakeHolder.role.id = :roleId")
    Set<StakeHolderLeaf> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT sl FROM StakeHolderLeaf sl WHERE sl.id IN " +
            "(SELECT r.target.id FROM Responsibility r WHERE r.source.id = :nodeId " +
            "AND r.percentage >= :minPercentage)")
    Set<StakeHolderLeaf> findDownstreamLeafNodesWithMinPercentage(
            @Param("nodeId") Long nodeId,
            @Param("minPercentage") double minPercentage);

    // In StakeHolderLeafRepository.java
    @Query("SELECT sl FROM StakeHolderLeaf sl WHERE sl.stakeHolder.id IN :stakeholderIds")
    List<StakeHolderLeaf> findByStakeholderIds(@Param("stakeholderIds") Collection<Long> stakeholderIds);
}
