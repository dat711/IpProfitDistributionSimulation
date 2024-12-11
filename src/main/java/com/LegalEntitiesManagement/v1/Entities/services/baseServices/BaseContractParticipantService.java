package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Common.proxies.BaseService;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractParticipantMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractParticipantNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractParticipantRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.List;
import java.util.Set;


public class BaseContractParticipantService implements BaseService<ContractParticipant, ContractParticipantDto, Long> {
    protected final ContractParticipantRepository participantRepository;
    protected final StakeHolderService stakeHolderService;
    protected final ContractParticipantMapper participantMapper = ContractParticipantMapper.INSTANCE;

    public BaseContractParticipantService(
            ContractParticipantRepository participantRepository,
            StakeHolderService stakeHolderService) {
        this.participantRepository = participantRepository;
        this.stakeHolderService = stakeHolderService;
    }

    @Override
    public ContractParticipant findById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ContractParticipantNotFoundException(id));
    }

    @Override
    public ContractParticipant save(ContractParticipant participant) {
        return participantRepository.save(participant);
    }

    @Override
    public ContractParticipant saveFromDto(ContractParticipantDto dto) {
        ContractParticipant participant = participantMapper.toEntity(dto);
        participant.setStakeholder(stakeHolderService.findById(dto.getStakeholderId()));
        // Contract will be set by composite layer
        return save(participant);
    }

    public Set<ContractParticipant> findParticipantsByContractId(Long contractId) {
        return participantRepository.findParticipantsByContractId(contractId);
    }

    @Override
    public boolean existsById(Long id) {
        return participantRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        // Not implemented as per requirements
    }

    @Override
    public List<ContractParticipant> findAll() {
        return participantRepository.findAll();
    }

    @Override
    public void verify(Object participantInfo) {
        ContractParticipant participant = UtilService.verifyAndGetEntity(
                participantInfo,
                ContractParticipant.class,
                ContractParticipantDto.class,
                participantMapper,
                "The object must belong to either ContractParticipant or ContractParticipantDto class"
        );
        if (!existsById(participant.getId())) {
            throw new ContractParticipantNotFoundException(participant.getId());
        }
    }

    public boolean isStakeholderParticipantInContract(Long contractId, Long stakeholderId){
        return participantRepository.isStakeholderParticipantInContract(contractId, stakeholderId);
    }

    public ContractParticipant findExecutorByContractId (Long contractId){
        return participantRepository.findExecutorByContractId(contractId).orElseThrow(() ->
                new RuntimeException(String.format("The Contract with id %s do not have an executor", contractId)));
    }
}
