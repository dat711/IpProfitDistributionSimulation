package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractNodeDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractNodeMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNodeNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.MoneyNode;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractNodeRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.*;
import java.util.stream.Collectors;

public class ContractNodeService implements BaseService<ContractNode, ContractNodeDto, Long> {
    private final ContractNodeRepository contractNodeRepository;
    private final ContractNodeMapper contractNodeMapper = ContractNodeMapper.INSTANCE;

    public ContractNodeService(ContractNodeRepository contractNodeRepository) {
        this.contractNodeRepository = contractNodeRepository;
    }

    @Override
    public ContractNode findById(Long id) {
        return contractNodeRepository.findById(id)
                .orElseThrow(() -> new ContractNodeNotFoundException(id));
    }

    @Override
    public ContractNode save(ContractNode node) {
        return contractNodeRepository.save(node);
    }

    @Override
    public ContractNode saveFromDto(ContractNodeDto dto) {
        ContractNode node = contractNodeMapper.toEntity(dto);
        return save(node);
    }

    @Override
    public boolean existsById(Long id) {
        return contractNodeRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.contractNodeRepository.deleteById(id);
    }

    @Override
    public List<ContractNode> findAll() {
        return contractNodeRepository.findAll();
    }

    @Override
    public void verify(Object nodeInfo) {
        ContractNode node = UtilService.verifyAndGetEntity(
                nodeInfo,
                ContractNode.class,
                ContractNodeDto.class,
                contractNodeMapper,
                "The object must belong to either ContractNode or ContractNodeDto class"
        );
        if (!existsById(node.getId())) {
            throw new ContractNodeNotFoundException(node.getId());
        }
    }

    // Custom repository methods
    public Optional<ContractNode> findByContractId(Long contractId) {
        return contractNodeRepository.findByContractId(contractId);
    }

    public Set<ContractNode> findDownstreamContractNodes(Long nodeId) {
        return contractNodeRepository.findDownstreamContractNodes(nodeId);
    }

    public Set<ContractNode> findUpstreamContractNodes(Long nodeId) {
        return contractNodeRepository.findUpstreamContractNodes(nodeId);
    }

    public ContractNode castContractNode(MoneyNode node){
        if (node instanceof ContractNode){
           return  (ContractNode)node;
        }
        throw new ContractNodeNotFoundException(node.getId());
    }

    public List<ContractNode> saveBatch(Iterable<ContractNode> contractNodes){
        return this.contractNodeRepository.saveAll(contractNodes);
    }

    public List<ContractNode> findByIds(Collection<Long> ids){
        return this.contractNodeRepository.findAllById(ids);
    }

    public List<ContractNode> findByContracts(Collection<? extends Contract> contracts){
        Set<Long> ids = contracts.stream().map(Contract::getId).collect(Collectors.toSet());
        return findByIds(ids);
    }
}
