package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Common.Proxies.Updatable;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.*;
import java.util.stream.Collectors;


public class StakeHolderService implements BaseService<StakeHolder, StakeHolderDto, Long>,
        Updatable<StakeHolder, StakeHolderDto> {
    private final StakeHolderRepository stakeHolderRepository;

    private final StakeHolderMapper stakeHolderMapper = StakeHolderMapper.INSTANCE;

    private final RoleService roleService;

    public StakeHolderService(StakeHolderRepository stakeHolderRepository, RoleService roleService) {
        this.stakeHolderRepository = stakeHolderRepository;
        this.roleService = roleService;
    }

    private StakeHolder DtoToEntity(StakeHolderDto stakeHolderDto){
        StakeHolder stakeHolder = this.stakeHolderMapper.toEntity(stakeHolderDto);
        Role role = this.roleService.findById(stakeHolderDto.getRoleId());
        stakeHolder.setRole(role);
        return stakeHolder;
    }

    @Override
    public StakeHolder findById(Long id) {
        return this.stakeHolderRepository.findById(id).orElseThrow(() -> new StakeHolderNotFoundException(id));
    }

    @Override
    public StakeHolder save(StakeHolder stakeHolder) {
        return this.stakeHolderRepository.save(stakeHolder);
    }

    @Override
    public boolean existsById(Long id) {
        return this.stakeHolderRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.stakeHolderRepository.deleteById(id);
    }

    @Override
    public List<StakeHolder> findAll() {
        return this.stakeHolderRepository.findAll();
    }

    @Override
    public StakeHolder saveFromDto(StakeHolderDto stakeHolderDto) {
        return this.save(DtoToEntity(stakeHolderDto));
    }

    @Override
    public StakeHolder update(StakeHolder stakeHolder) {
        return this.save(stakeHolder);
    }

    @Override
    public StakeHolder updateFromDto(StakeHolderDto stakeHolderDto) {
        return this.update(DtoToEntity(stakeHolderDto));
    }

    @Override
    public void verify(Object stakeHolderInfo){
        StakeHolder stakeHolder = UtilService.verifyAndGetEntity(stakeHolderInfo,StakeHolder.class, StakeHolderDto.class,
                stakeHolderMapper, "The object must belong to either StakeHolder or StakeHolderDto class");
        if (!this.existsById(stakeHolder.getId())){
            throw new StakeHolderNotFoundException(stakeHolder.getId());
        }
    }

    // In service layer
    public List<StakeHolder> findAndVerifyAll(Collection<Long> stakeholderIds) {
        List<StakeHolder> found = stakeHolderRepository.findAllById(stakeholderIds);

        if (found.size() != stakeholderIds.size()) {
            Set<Long> foundIds = found.stream()
                    .map(StakeHolder::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(stakeholderIds);
            missingIds.removeAll(foundIds);

            throw new StakeHolderNotFoundException(
                    String.format("Could not find stakeholders with ids: %s",
                            String.join(", ", missingIds.stream().map(String::valueOf).toList()))
            );
        }

        return found;
    }
}
