package com.LegalEntitiesManagement.v1.Entities.services;

import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import org.springframework.stereotype.Service;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import org.springframework.transaction.annotation.Transactional;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.RoleMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IntellectualPropertyMapper;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EntitiesCrudService {
    private final IntellectualPropertyService intellectualPropertyService;
    private final StakeHolderService stakeHolderService;
    private final RoleService roleService;

    private final BaseContractParticipantService baseContractParticipantService;

    private final StakeHolderLeafService stakeHolderLeafService;

    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    private final StakeHolderMapper stakeHolderMapper = StakeHolderMapper.INSTANCE;

    private final IntellectualPropertyMapper intellectualPropertyMapper = IntellectualPropertyMapper.INSTANCE;

    public EntitiesCrudService(IntellectualPropertyService intellectualPropertyService, StakeHolderService stakeHolderService, RoleService roleService, BaseContractParticipantService baseContractParticipantService, StakeHolderLeafService stakeHolderLeafService) {
        this.intellectualPropertyService = intellectualPropertyService;
        this.stakeHolderService = stakeHolderService;
        this.roleService = roleService;
        this.baseContractParticipantService = baseContractParticipantService;
        this.stakeHolderLeafService = stakeHolderLeafService;
    }


    @Transactional
    public RoleDto addRole(RoleDto roleDto){
        Role savedRole = this.roleService.saveFromDto(roleDto);
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    public RoleDto getRole(Long id) {
        Role queriedRole = this.roleService.findById(id);
        return roleMapper.toDto(queriedRole);
    }

    @Transactional
    public List<RoleDto> getAllRoles() {
        List<Role> roles = this.roleService.findAll();
        return roleMapper.toDtoList(roles);
    }

    // IntellectualProperty CRUD operations
    public IntellectualPropertyDto addIntellectualProperty(IntellectualPropertyDto ipDto) {
        IntellectualProperty savedIp = this.intellectualPropertyService.saveFromDto(ipDto);
        return intellectualPropertyMapper.toDto(savedIp);
    }

    public IntellectualPropertyDto updateIntellectualProperty(IntellectualPropertyDto ipDto) {
        this.intellectualPropertyService.verify(ipDto);
        IntellectualProperty updatedIp = this.intellectualPropertyService.updateFromDto(ipDto);
        return intellectualPropertyMapper.toDto(updatedIp);
    }

    public IntellectualPropertyDto getIntellectualProperty(Long id) {
        IntellectualProperty ip = this.intellectualPropertyService.findById(id);
        return intellectualPropertyMapper.toDto(ip);
    }

    public List<IntellectualPropertyDto> getAllIntellectualProperties() {
        List<IntellectualProperty> ips = this.intellectualPropertyService.findAll();
        return intellectualPropertyMapper.toDtoList(ips);
    }

    // StakeHolder CRUD operations
    public StakeHolderDto addStakeHolder(StakeHolderDto stakeHolderDto) {
        // Verify that the role exists before creating the StakeHolder
        this.roleService.findById(stakeHolderDto.getRoleId());
        StakeHolder savedStakeHolder = this.stakeHolderService.saveFromDto(stakeHolderDto);
        StakeHolderLeaf leaf = new StakeHolderLeaf(savedStakeHolder);
        stakeHolderLeafService.save(leaf);
        return stakeHolderMapper.toDto(savedStakeHolder);
    }

    public StakeHolderDto updateStakeHolder(StakeHolderDto stakeHolderDto) {
        // Verify both the StakeHolder and its role exist before updating
        this.stakeHolderService.verify(stakeHolderDto);
        this.roleService.findById(stakeHolderDto.getRoleId());
        StakeHolder updatedStakeHolder = this.stakeHolderService.updateFromDto(stakeHolderDto);
        return stakeHolderMapper.toDto(updatedStakeHolder);
    }

    public StakeHolderDto getStakeHolder(Long id) {
        StakeHolder stakeHolder = this.stakeHolderService.findById(id);
        return stakeHolderMapper.toDto(stakeHolder);
    }

    public List<StakeHolderDto> getAllStakeHolders() {
        List<StakeHolder> stakeHolders = this.stakeHolderService.findAll();
        return stakeHolderMapper.toDtoList(stakeHolders);
    }

    public boolean roleExists(Long id) {
        return roleService.existsById(id);
    }

    public boolean stakeholderExists(Long id) {
        return stakeHolderService.existsById(id);
    }

    public boolean intellectualPropertyExists(Long id) {
        return intellectualPropertyService.existsById(id);
    }

    public void deleteStakeHolder(Long id){
        this.stakeHolderService.deleteById(id);
    }

    public Set<ContractParticipant> findParticipantsByStakeHolderId(Long id){
        return this.baseContractParticipantService.findByStakeholderId(id);
    }
}
