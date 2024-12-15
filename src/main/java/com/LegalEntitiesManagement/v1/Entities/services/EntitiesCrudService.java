package com.LegalEntitiesManagement.v1.Entities.services;

import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.RoleNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderNotFoundException;
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

    private final BaseContractService contractService;

    private final GraphBuilderService graphBuilderService;

    private final IpBasedContractService ipBasedContractService;

    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    private final StakeHolderMapper stakeHolderMapper = StakeHolderMapper.INSTANCE;

    private final IntellectualPropertyMapper intellectualPropertyMapper = IntellectualPropertyMapper.INSTANCE;

    public EntitiesCrudService(IntellectualPropertyService intellectualPropertyService, StakeHolderService stakeHolderService, RoleService roleService, BaseContractParticipantService baseContractParticipantService, StakeHolderLeafService stakeHolderLeafService, BaseContractService contractService, GraphBuilderService graphBuilderService, IpBasedContractService ipBasedContractService) {
        this.intellectualPropertyService = intellectualPropertyService;
        this.stakeHolderService = stakeHolderService;
        this.roleService = roleService;
        this.baseContractParticipantService = baseContractParticipantService;
        this.stakeHolderLeafService = stakeHolderLeafService;
        this.contractService = contractService;
        this.graphBuilderService = graphBuilderService;
        this.ipBasedContractService = ipBasedContractService;
    }

    public RoleDto addRole(RoleDto roleDto){
        Role savedRole = this.roleService.saveFromDto(roleDto);
        return roleMapper.toDto(savedRole);
    }

    public RoleDto getRole(Long id) {
        Role queriedRole = this.roleService.findById(id);
        return roleMapper.toDto(queriedRole);
    }

    public List<RoleDto> getAllRoles() {
        List<Role> roles = this.roleService.findAll();
        return roleMapper.toDtoList(roles);
    }

    public RoleDto updateRole(RoleDto roleDto){
        Role updatedRole = this.roleService.updateFromDto(roleDto);
        return roleMapper.toDto(updatedRole);
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
        if (!this.stakeholderExists(id)){
            throw new StakeHolderNotFoundException(id);
        }

        if (!this.findParticipantsByStakeHolderId(id).isEmpty()){
            throw new IllegalArgumentException("Exist StakeHolders depend on the attempted delete Role");
        }
        this.stakeHolderService.deleteById(id);
    }

    public void deleteRole(Long id){
        if (!this.roleExists(id)){
            throw new RoleNotFoundException(id);
        }

        if (this.stakeHolderExistByRoleId(id)){
            throw new IllegalArgumentException("Exist StakeHolders depend on the attempted delete Role");
        }
        this.roleService.deleteById(id);
    }

    public void deleteIntellectualProperty(Long id){
        if(!this.intellectualPropertyService.existsById(id)){
            throw new IpNotFoundException(id);
        }

        if (this.ipBasedContractService.existByIpId(id)){
            throw new IllegalArgumentException("Exist contract associate with this Ip");
        }

        this.intellectualPropertyService.deleteById(id);
    }

    public boolean stakeHolderExistByRoleId(Long id){
        return this.stakeHolderService.existByRoleId(id);
    }

    public Set<ContractParticipant> findParticipantsByStakeHolderId(Long id){
        return this.baseContractParticipantService.findByStakeholderId(id);
    }
}
