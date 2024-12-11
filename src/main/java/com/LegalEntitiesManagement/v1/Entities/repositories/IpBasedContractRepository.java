package com.LegalEntitiesManagement.v1.Entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IpBasedContractRepository extends JpaRepository<IpBasedContract, Long>{

    @Query("SELECT ibc FROM IpBasedContract ibc WHERE ibc.intellectualProperty.id = :ipId")
    Set<IpBasedContract> getIpBasedContractByIpId(@Param("ipId") Long IpId);

    @Query("SELECT cp.contract FROM ContractParticipant cp WHERE cp.stakeholder.id = :stakeholderId AND TYPE(cp.contract) = IpBasedContract")
    Set<IpBasedContract> findIpBasedContractsByStakeholderId(@Param("stakeholderId") Long stakeholderId);

    // Reusing the same query pattern as ContractRepository, just with IpBasedContract type
    @Query("SELECT c FROM profit_distribution_contract c WHERE c.executor.id = :stakeholderId AND TYPE(c) = IpBasedContract")
    Set<IpBasedContract> findIpBasedContractsWhereStakeholderIsExecutor(@Param("stakeholderId") Long stakeholderId);

    @Query("SELECT c FROM profit_distribution_contract c WHERE c.id IN :contractIds AND TYPE(c) = IpBasedContract")
    List<IpBasedContract> findByIds(@Param("contractIds") Collection<Long> contractIds);
}
