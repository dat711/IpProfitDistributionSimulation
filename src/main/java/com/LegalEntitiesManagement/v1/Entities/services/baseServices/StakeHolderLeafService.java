package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderLeafDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderLeafMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderLeafNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderLeafRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;
import com.LegalEntitiesManagement.v1.Entities.exceptions.GraphViolatedException.StakeHolderLeafNotRegisteredException;

import java.util.*;
import java.util.stream.Collectors;

public class StakeHolderLeafService implements BaseService<StakeHolderLeaf, StakeHolderLeafDto, Long> {
    private final StakeHolderLeafRepository stakeHolderLeafRepository;
    private final StakeHolderLeafMapper stakeHolderLeafMapper = StakeHolderLeafMapper.INSTANCE;

    public StakeHolderLeafService(StakeHolderLeafRepository stakeHolderLeafRepository) {
        this.stakeHolderLeafRepository = stakeHolderLeafRepository;
    }

    @Override
    public StakeHolderLeaf findById(Long id) {
        return stakeHolderLeafRepository.findById(id)
                .orElseThrow(() -> new StakeHolderLeafNotFoundException(id));
    }

    @Override
    public StakeHolderLeaf save(StakeHolderLeaf leaf) {
        return stakeHolderLeafRepository.save(leaf);
    }

    @Override
    public StakeHolderLeaf saveFromDto(StakeHolderLeafDto dto) {
        StakeHolderLeaf leaf = stakeHolderLeafMapper.toEntity(dto);
        return save(leaf);
    }

    @Override
    public boolean existsById(Long id) {
        return stakeHolderLeafRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        // Not implemented as per requirements
    }

    @Override
    public List<StakeHolderLeaf> findAll() {
        return stakeHolderLeafRepository.findAll();
    }

    @Override
    public void verify(Object leafInfo) {
        StakeHolderLeaf leaf = UtilService.verifyAndGetEntity(
                leafInfo,
                StakeHolderLeaf.class,
                StakeHolderLeafDto.class,
                stakeHolderLeafMapper,
                "The object must belong to either StakeHolderLeaf or StakeHolderLeafDto class"
        );
        if (!existsById(leaf.getId())) {
            throw new StakeHolderLeafNotFoundException(leaf.getId());
        }
    }

    // Repository-specific methods
    public StakeHolderLeaf findByStakeholderId(Long stakeholderId) {
        return stakeHolderLeafRepository.findByStakeholderId(stakeholderId).orElseThrow(
                () -> new StakeHolderLeafNotRegisteredException(stakeholderId));
    }

    public Set<StakeHolderLeaf> findLeafNodesForContractNode(Long contractNodeId) {
        return stakeHolderLeafRepository.findLeafNodesForContractNode(contractNodeId);
    }

    public Set<StakeHolderLeaf> findByRoleId(Long roleId) {
        return stakeHolderLeafRepository.findByRoleId(roleId);
    }

    public Set<StakeHolderLeaf> findDownstreamLeafNodesWithMinPercentage(Long nodeId, double minPercentage) {
        return stakeHolderLeafRepository.findDownstreamLeafNodesWithMinPercentage(nodeId, minPercentage);
    }

    public List<StakeHolderLeaf> saveAll(List<StakeHolderLeaf> leafs){
        return this.stakeHolderLeafRepository.saveAll(leafs);
    }

    public Map<StakeHolder, StakeHolderLeaf> getLeaves (Collection<StakeHolder> stakeHolders){
        List<Long> ids = stakeHolders.stream().map(StakeHolder::getId).toList();
        List<StakeHolderLeaf> leaves = this.stakeHolderLeafRepository.findByStakeholderIds(ids);
        if (leaves.size() != stakeHolders.size()){
            Set<StakeHolder> allStakeHolder = new HashSet<>(stakeHolders);
            Set<StakeHolder> foundStakeHolder = leaves.stream().map(StakeHolderLeaf::getStakeHolder).collect(Collectors.toSet());
            allStakeHolder.retainAll(foundStakeHolder);
            throw new StakeHolderLeafNotRegisteredException(
                    String.format("Could not find stakeholderLeaf register with stakeholder by ids: %s",
                            String.join(", ", allStakeHolder.stream().map(stakeHolder -> stakeHolder.getId().toString()).toList()))
            );
        }
        return leaves.stream().collect(Collectors.toMap(StakeHolderLeaf::getStakeHolder,leaf -> leaf));
    }


}
