package com.LegalEntitiesManagement.v1.Entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ContractRepository extends JpaRepository<Contract, Long>{
    @Query("SELECT cp.contract FROM ContractParticipant cp WHERE cp.stakeholder.id = :stakeholderId")
    Set<Contract> findContractsByStakeholderId(@Param("stakeholderId") Long stakeholderId);

    // Find all contracts where a stakeholder is an executor
    @Query("SELECT c FROM profit_distribution_contract c WHERE c.executor.id = :stakeholderId")
    Set<Contract> findContractsWhereStakeholderIsExecutor(@Param("stakeholderId") Long stakeholderId);

    @Query("SELECT c FROM profit_distribution_contract c WHERE c.id IN :contractIds")
    List<Contract> findByIds(@Param("contractIds") Collection<Long> contractIds);
}
