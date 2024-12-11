package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.exceptions.RoleNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.RoleService;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.RoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class RoleServiceTest extends BaseServiceTestMockedDependencies {
    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    private RoleDto testRoleDto;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Call base class setup first
        super.baseSetUp();

        // Create test data
        testRoleDto = new RoleDto();
        testRoleDto.setId(1L);
        testRoleDto.setName("Tax Collector");
        testRoleDto.setDescription("They collect tax");

        testRole = new Role("Tax Collector", "They collect tax", 6);
        testRole.setId(1L);
    }

    @Test
    void save_ValidRoleDto_ReturnsSavedRole() {
        // Arrange
        when(roleMapper.toEntity(any(RoleDto.class))).thenReturn(testRole);
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role savedRole = roleService.saveFromDto(testRoleDto);

        // Assert
        assertNotNull(savedRole);
        assertEquals(testRoleDto.getName(), savedRole.getName());
        assertEquals(testRoleDto.getDescription(), savedRole.getDescription());
        verify(roleMapper, times(1)).toEntity(testRoleDto);
        verify(roleRepository, times(1)).save(testRole);
    }

    @Test
    void findById_ExistingId_ReturnsRole() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // Act
        Role foundRole = roleService.findById(1L);

        // Assert
        assertNotNull(foundRole);
        assertEquals(testRole.getId(), foundRole.getId());
        assertEquals(testRole.getName(), foundRole.getName());
        assertEquals(testRole.getDescription(), foundRole.getDescription());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void existsById_ExistingId_ReturnsTrue() {
        // Arrange
        when(roleRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean exists = roleService.existsById(1L);

        // Assert
        assertTrue(exists);
        verify(roleRepository, times(1)).existsById(1L);
    }

    @Test
    void findAll_ReturnsAllRoles() {
        // Arrange
        Role secondRole = new Role("Administrator", "System administrator", 3);
        secondRole.setId(2L);
        List<Role> roles = Arrays.asList(testRole, secondRole);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<Role> foundRoles = roleService.findAll();

        // Assert
        assertNotNull(foundRoles);
        assertEquals(2, foundRoles.size());
        assertEquals(testRole.getName(), foundRoles.get(0).getName());
        assertEquals(secondRole.getName(), foundRoles.get(1).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void save_ValidRole_ReturnsSavedRole() {
        // Arrange
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role savedRole = roleService.save(testRole);

        // Assert
        assertNotNull(savedRole);
        assertEquals(testRole.getName(), savedRole.getName());
        assertEquals(testRole.getDescription(), savedRole.getDescription());
        assertEquals(testRole.getPriority(), savedRole.getPriority());
        verify(roleRepository, times(1)).save(testRole);
    }

    @Test
    void updateFromDto_ValidDto_ReturnsUpdatedRole() {
        // Arrange
        RoleDto roleDto = new RoleDto(1L, "Admin", "Updated description", 2);
        Role role = new Role("Admin", "Updated description", 2);
        role.setId(1L);
        when(roleMapper.toEntity(roleDto)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        Role updatedRole = roleService.updateFromDto(roleDto);

        // Assert
        assertNotNull(updatedRole);
        assertEquals(roleDto.getId(), updatedRole.getId());
        assertEquals(roleDto.getName(), updatedRole.getName());
        assertEquals(roleDto.getDescription(), updatedRole.getDescription());
        assertEquals(roleDto.getPriority(), updatedRole.getPriority());
        verify(roleMapper).toEntity(roleDto);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void verify_ValidRole_NoException() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> roleService.verify(role));
        verify(roleRepository).existsById(1L);
    }

    @Test
    void verify_ValidRoleDto_NoException() {
        // Arrange
        RoleDto roleDto = new RoleDto(1L, "Admin", "Description", 2);
        Role role = new Role("Admin", "Description", 2);
        role.setId(1L);
        when(roleMapper.toEntity(roleDto)).thenReturn(role);
        when(roleRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> roleService.verify(roleDto));
        verify(roleRepository).existsById(1L);
    }

    @Test
    void verify_InvalidType_ThrowsTypeNotMatchException() {
        // Arrange
        String invalidObject = "Invalid type";

        // Act & Assert
        assertThrows(TypeNotMatchException.class, () -> roleService.verify(invalidObject));
    }

    @Test
    void verify_NonExistingRole_ThrowsRoleNotFoundException() {
        // Arrange
        Role role = new Role();
        role.setId(999L);
        when(roleRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RoleNotFoundException.class, () -> roleService.verify(role));
        verify(roleRepository).existsById(999L);
    }
}
