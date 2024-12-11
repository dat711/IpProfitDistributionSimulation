package com.LegalEntitiesManagement.v1.Entities.repositories;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
public interface ContractNodeRepository extends JpaRepository<ContractNode, Long>{
    @Query("SELECT cn FROM ContractNode cn WHERE cn.contract.id = :contractId")
    Optional<ContractNode> findByContractId(@Param("contractId") Long contractId);

    @Query("SELECT cn FROM ContractNode cn WHERE cn.id IN " +
            "(SELECT r.target.id FROM Responsibility r WHERE r.source.id = :nodeId)")
    Set<ContractNode> findDownstreamContractNodes(@Param("nodeId") Long nodeId);

    @Query("SELECT cn FROM ContractNode cn WHERE cn.id IN " +
            "(SELECT r.source.id FROM Responsibility r WHERE r.target.id = :nodeId)")
    Set<ContractNode> findUpstreamContractNodes(@Param("nodeId") Long nodeId);
}
