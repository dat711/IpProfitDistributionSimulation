package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Entities.dto.mapper.RoleMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.RoleNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Common.Proxies.Updatable;

import java.util.List;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;
import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements BaseService<Role, RoleDto, Long>, Updatable<Role, RoleDto> {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Role findById(Long id) {
        return this.roleRepository.findById(id).orElseThrow(()->new RoleNotFoundException(id));
    }

    @Override
    public Role saveFromDto(RoleDto roleDto) {
        Role role = this.roleMapper.toEntity(roleDto);
        return this.roleRepository.save(role);
    }

    @Override
    public Role save(Role role) {return this.roleRepository.save(role);}

    @Override
    public boolean existsById(Long id) {
        return this.roleRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    @Override
    public Role update(Role role){
        return this.save(role);
    }

    @Override
    public Role updateFromDto(RoleDto roleDto){
        Role role = this.roleMapper.toEntity(roleDto);
        return this.update(role);
    }

    @Override
    public void verify(Object roleInfo) throws RoleNotFoundException{
        Role role = UtilService.verifyAndGetEntity(roleInfo, Role.class, RoleDto.class, roleMapper,
                "The object must belong to either the Role or RoleDto class");
        if (!this.existsById(role.getId())){
            throw new RoleNotFoundException(role.getId());
        }
    }
}
