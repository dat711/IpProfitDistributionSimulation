package com.LegalEntitiesManagement.v1.Entities.services.baseServices;
import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Common.Proxies.Updatable;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IpBasedContractMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpBasedContractRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.List;
import java.util.Set;

public class IpBasedContractService implements BaseService<IpBasedContract, IpBasedContractDto, Long>,
        Updatable<IpBasedContract, IpBasedContractDto>{
    private final IpBasedContractRepository ipBasedContractRepository;
    private final IpBasedContractMapper ipBasedContractMapper = IpBasedContractMapper.INSTANCE;

    public IpBasedContractService(IpBasedContractRepository ipBasedContractRepository) {
        this.ipBasedContractRepository = ipBasedContractRepository;
    }

    @Override
    public IpBasedContract findById(Long id) {
        return ipBasedContractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
    }

    @Override
    public IpBasedContract save(IpBasedContract contract) {
        return ipBasedContractRepository.save(contract);
    }

    @Override
    public IpBasedContract saveFromDto(IpBasedContractDto contractDto) {
        IpBasedContract contract = ipBasedContractMapper.toEntity(contractDto);
        return save(contract);
    }

    @Override
    public IpBasedContract update(IpBasedContract contract) {
        return save(contract);
    }

    @Override
    public IpBasedContract updateFromDto(IpBasedContractDto contractDto) {
        return update(ipBasedContractMapper.toEntity(contractDto));
    }

    @Override
    public boolean existsById(Long id) {
        return ipBasedContractRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.ipBasedContractRepository.deleteById(id);
    }

    @Override
    public List<IpBasedContract> findAll() {
        return ipBasedContractRepository.findAll();
    }

    public Set<IpBasedContract> findContractsByIpId(Long ipId) {
        return ipBasedContractRepository.getIpBasedContractByIpId(ipId);
    }

    @Override
    public void verify(Object contractInfo) {
        IpBasedContract contract = UtilService.verifyAndGetEntity(
                contractInfo,
                IpBasedContract.class,
                IpBasedContractDto.class,
                ipBasedContractMapper,
                "The object must belong to either IpBasedContract or IpBasedContractDto class"
        );
        if (!existsById(contract.getId())) {
            throw new ContractNotFoundException(contract.getId());
        }
    }

    public Set<IpBasedContract> findIpBasedContractsByStakeholderId(Long stakeholderId){
        return ipBasedContractRepository.findIpBasedContractsByStakeholderId(stakeholderId);
    }

    public Set<IpBasedContract> findIpBasedContractsWhereStakeholderIsExecutor(Long stakeholderId){
        return ipBasedContractRepository.findIpBasedContractsWhereStakeholderIsExecutor(stakeholderId);
    }

    public boolean existByIpId(Long ipId){
        return !this.ipBasedContractRepository.getIpBasedContractByIpId(ipId).isEmpty();
    }

    public List<IpBasedContract> saveAll(List<IpBasedContract> ipBasedContracts){
        return this.ipBasedContractRepository.saveAll(ipBasedContracts);
    }

    public boolean contractsHavingSameIp(Set<IpBasedContract> contracts){
        if (contracts.isEmpty()){
            return false;
        }
        if (contracts.size()  == 1 ){
            return true;
        }
        return contracts.stream().map(IpBasedContract::getIntellectualProperty).map(IntellectualProperty::getId).distinct().count() == 1;
    }
}
