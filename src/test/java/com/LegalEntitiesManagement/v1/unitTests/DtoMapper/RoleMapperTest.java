package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.RoleMapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleMapperTest {

    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    @Test
    void shouldMapRoleToRoleDto() {
        // given
        Role role = new Role("Admin", "Administrator role", 2);

        // when
        RoleDto roleDto = roleMapper.toDto(role);

        // then
        assertNotNull(roleDto);
        assertEquals(role.getName(), roleDto.getName());
        assertEquals(role.getDescription(), roleDto.getDescription());
        assertEquals(role.getPriority(), roleDto.getPriority());
    }

    @Test
    void shouldMapRoleDtoToRole() {
        // given
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setName("Admin");
        roleDto.setDescription("Administrator role");

        // when
        Role role = roleMapper.toEntity(roleDto);

        // then
        assertNotNull(role);
        assertEquals(roleDto.getName(), role.getName());
        assertEquals(roleDto.getDescription(), role.getDescription());
        assertEquals(roleDto.getPriority(), role.getPriority());
    }

    @Test
    void shouldHandleNullRole() {
        // when
        RoleDto roleDto = roleMapper.toDto(null);
        // then
        assertNull(roleDto);
    }

}
