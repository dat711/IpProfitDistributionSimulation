package com.LegalEntitiesManagement.v1.Entities.repositories;

import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ContractParticipantRepository extends JpaRepository<ContractParticipant, Long> {
    @Query("SELECT cp FROM ContractParticipant cp WHERE cp.contract.id = :contractId")
    Set<ContractParticipant> findParticipantsByContractId(@Param("contractId") Long contractId);

    // Find the executor for a specific contract
    @Query("SELECT cp FROM ContractParticipant cp WHERE cp.contract.id = :contractId AND cp.isExecutor = true")
    Optional<ContractParticipant> findExecutorByContractId(@Param("contractId") Long contractId);

    // Check if a stakeholder is a participant in a specific contract
    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM ContractParticipant cp " +
            "WHERE cp.contract.id = :contractId AND cp.stakeholder.id = :stakeholderId")
    boolean isStakeholderParticipantInContract(
            @Param("contractId") Long contractId,
            @Param("stakeholderId") Long stakeholderId
    );

    // In ContractParticipantRepository.java
    @Query("SELECT cp FROM ContractParticipant cp WHERE cp.stakeholder.id = :stakeholderId")
    Set<ContractParticipant> findByStakeholderId(@Param("stakeholderId") Long stakeholderId);

    @Query("SELECT cp FROM ContractParticipant cp WHERE cp.contract.id IN :contractIds")
    Set<ContractParticipant> findParticipantsByContractIds(@Param("contractIds") Collection<Long> contractIds);

    @Query("DELETE FROM ContractParticipant cp WHERE cp.contract.id = :contractId")
    @Modifying
    void deleteAllByContractId(@Param("contractId") Long contractId);
}
