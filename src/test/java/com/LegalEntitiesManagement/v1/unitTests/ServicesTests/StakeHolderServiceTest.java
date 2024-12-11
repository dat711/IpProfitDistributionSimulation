package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.RoleService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.StakeHolderService;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StakeHolderServiceTest extends BaseServiceTestMockedDependencies {
    @Mock
    private StakeHolderMapper stakeHolderMapper;

    @Mock
    private RoleService roleService;

    private StakeHolderService stakeHolderService;
    private StakeHolder mockStakeHolder;
    private StakeHolderDto mockStakeHolderDto;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        stakeHolderService = new StakeHolderService(stakeHolderRepository, roleService);


        // Set up mock Role
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("Publisher");
        mockRole.setDescription("Music Publisher Role");

        // Set up mock StakeHolder
        mockStakeHolder = new StakeHolder();
        mockStakeHolder.setId(1L);
        mockStakeHolder.setName("Warner Music");
        mockStakeHolder.setRole(mockRole);

        // Set up mock StakeHolderDto
        mockStakeHolderDto = new StakeHolderDto();
        mockStakeHolderDto.setId(1L);
        mockStakeHolderDto.setName("Warner Music");
        mockStakeHolderDto.setRoleId(1L);
    }

    @Test
    void findById_WhenStakeHolderExists_ShouldReturnStakeHolder() {
        // Arrange
        when(stakeHolderRepository.findById(mockStakeHolder.getId())).thenReturn(Optional.of(mockStakeHolder));

        // Act
        StakeHolder result = stakeHolderService.findById(mockStakeHolder.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockStakeHolder.getName(), result.getName());
        assertEquals(mockStakeHolder.getRole(), result.getRole());
        verify(stakeHolderRepository).findById(mockStakeHolder.getId());
    }

    @Test
    void findById_WhenStakeHolderDoesNotExist_ShouldThrowException() {
        // Arrange
        when(stakeHolderRepository.findById(mockStakeHolder.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(StakeHolderNotFoundException.class,
                () -> stakeHolderService.findById(mockStakeHolder.getId()));
        verify(stakeHolderRepository).findById(mockStakeHolder.getId());
    }

    @Test
    void save_WhenValidStakeHolder_ShouldReturnSavedStakeHolder() {
        // Arrange
        when(stakeHolderRepository.save(mockStakeHolder)).thenReturn(mockStakeHolder);

        // Act
        StakeHolder result = stakeHolderService.save(mockStakeHolder);

        // Assert
        assertNotNull(result);
        assertEquals(mockStakeHolder.getName(), result.getName());
        assertEquals(mockStakeHolder.getRole(), result.getRole());
        verify(stakeHolderRepository).save(mockStakeHolder);
    }

    @Test
    void saveFromDto_WhenValidDto_ShouldReturnSavedStakeHolder() {
        // Arrange
        when(roleService.findById(mockStakeHolderDto.getRoleId())).thenReturn(mockRole);
        when(stakeHolderRepository.save(any(StakeHolder.class))).thenReturn(mockStakeHolder);

        // Act
        StakeHolder result = stakeHolderService.saveFromDto(mockStakeHolderDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockStakeHolderDto.getName(), result.getName());
        assertEquals(mockStakeHolderDto.getRoleId(), result.getRole().getId());
        verify(stakeHolderRepository).save(any(StakeHolder.class));
    }

    @Test
    void findAll_ShouldReturnAllStakeHolders() {
        // Arrange
        List<StakeHolder> stakeHolders = Arrays.asList(mockStakeHolder,
                new StakeHolder("Sony Music", mockRole));
        when(stakeHolderRepository.findAll()).thenReturn(stakeHolders);

        // Act
        List<StakeHolder> result = stakeHolderService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(stakeHolderRepository).findAll();
    }

    @Test
    void existsById_WhenStakeHolderExists_ShouldReturnTrue() {
        // Arrange
        when(stakeHolderRepository.existsById(mockStakeHolder.getId())).thenReturn(true);

        // Act
        boolean result = stakeHolderService.existsById(mockStakeHolder.getId());

        // Assert
        assertTrue(result);
        verify(stakeHolderRepository).existsById(mockStakeHolder.getId());
    }

    @Test
    void verify_WhenValidStakeHolder_ShouldNotThrowException() {
        // Arrange
        when(stakeHolderRepository.existsById(mockStakeHolder.getId())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> stakeHolderService.verify(mockStakeHolder));
    }

    @Test
    void verify_WhenValidStakeHolderDto_ShouldNotThrowException() {
        // Arrange
        when(stakeHolderRepository.existsById(mockStakeHolder.getId())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> stakeHolderService.verify(mockStakeHolderDto));
    }

    @Test
    void verify_WhenInvalidType_ShouldThrowException() {
        // Arrange
        String invalidType = "invalid";

        // Act & Assert
        assertThrows(TypeNotMatchException.class, () -> stakeHolderService.verify(invalidType));
    }

    @Test
    void verify_WhenStakeHolderNotFound_ShouldThrowException() {
        // Arrange
        when(stakeHolderRepository.existsById(mockStakeHolder.getId())).thenReturn(false);

        // Act & Assert
        assertThrows(StakeHolderNotFoundException.class, () -> stakeHolderService.verify(mockStakeHolder));
    }

    @Test
    void updateFromDto_WhenValidDto_ShouldReturnUpdatedStakeHolder() {
        // Arrange
        when(stakeHolderRepository.save(any(StakeHolder.class))).thenReturn(mockStakeHolder);

        // Act
        StakeHolder result = stakeHolderService.updateFromDto(mockStakeHolderDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockStakeHolder.getName(), result.getName());
        verify(stakeHolderRepository).save(any(StakeHolder.class));
    }

    @Test
    void update_WhenValidStakeHolder_ShouldReturnUpdatedStakeHolder() {
        // Arrange
        when(stakeHolderRepository.save(mockStakeHolder)).thenReturn(mockStakeHolder);

        // Act
        StakeHolder result = stakeHolderService.update(mockStakeHolder);

        // Assert
        assertNotNull(result);
        assertEquals(mockStakeHolder.getName(), result.getName());
        assertEquals(mockStakeHolder.getRole(), result.getRole());
        verify(stakeHolderRepository).save(mockStakeHolder);
    }
}
