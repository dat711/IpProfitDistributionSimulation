package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Common.Proxies.Updatable;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BaseContractService implements BaseService<Contract, ContractDto, Long>,
        Updatable<Contract, ContractDto>{
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper = ContractMapper.INSTANCE;

    public BaseContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
    }

    @Override
    public Contract save(Contract contract) {
        Contract saved = contractRepository.save(contract);
        contractRepository.flush();
        return saved;
    }

    @Override
    public Contract saveFromDto(ContractDto contractDto) {
        Contract contract = contractMapper.toEntity(contractDto);
        return save(contract);
    }

    @Override
    public boolean existsById(Long id) {
        return contractRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.contractRepository.deleteById(id);
    }

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    public Contract update(Contract contract) {
        return save(contract);
    }

    @Override
    public Contract updateFromDto(ContractDto contractDto) {
        return update(contractMapper.toEntity(contractDto));
    }

    @Override
    public void verify(Object contractInfo) {
        Contract contract = UtilService.verifyAndGetEntity(
                contractInfo,
                Contract.class,
                ContractDto.class,
                contractMapper,
                "The object must belong to either Contract or ContractDto class"
        );
        if (!existsById(contract.getId())) {
            throw new ContractNotFoundException(contract.getId());
        }
    }

    public Set<Contract> findContractsParticipatedByStakeHolder(Long stakeholderId){
        return this.contractRepository.findContractsByStakeholderId(stakeholderId);
    }

    public Set<Contract> findContractsWhereStakeholderIsExecutor(Long stakeholderId){
        return this.contractRepository.findContractsWhereStakeholderIsExecutor(stakeholderId);
    }
}
