package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.RoleMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.exceptions.*;
import com.LegalEntitiesManagement.v1.Entities.dto.*;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("UtilService Tests")
public class UtilServiceTest {
    private static final String ERROR_MESSAGE = "Invalid type provided";
    private static final String FULL_ERROR = String.format("The Type is not match system requirements with details: %s", "Invalid type provided");

    @Nested
    @DisplayName("Role Entity Conversion Tests")
    class RoleConversionTests{
        @Mock
        private RoleMapper roleMapper;

        private final String roleName = "Admin";
        private final String roleDescription = "Administrator";
        private final Long roleId = 1L;

        private final int rolePriority = 3;
        private Role roleEntity;
        private RoleDto roleDto;

        @BeforeEach
        void setUp() {
            roleEntity = new Role(roleName, roleDescription, rolePriority);
            roleEntity.setId(roleId);

            roleDto = new RoleDto(roleId, roleName, roleDescription, rolePriority);
        }

        @Test
        @DisplayName("Should return same Role entity when passed a Role")
        void whenPassedRoleEntity_shouldReturnSameEntity() {
            Role result = UtilService.verifyAndGetEntity(
                    roleEntity,
                    Role.class,
                    RoleDto.class,
                    roleMapper,
                    ERROR_MESSAGE
            );

            assertNotNull(result);
            assertEquals(roleEntity.getId(), result.getId());
            assertEquals(roleEntity.getName(), result.getName());
            assertEquals(roleEntity.getDescription(), result.getDescription());
        }

        @Test
        @DisplayName("Should convert RoleDto to Role entity")
        void whenPassedRoleDto_shouldConvertToEntity() {
            when(roleMapper.toEntity(any(RoleDto.class))).thenReturn(roleEntity);

            Role result = UtilService.verifyAndGetEntity(
                    roleDto,
                    Role.class,
                    RoleDto.class,
                    roleMapper,
                    ERROR_MESSAGE
            );

            assertNotNull(result);
            assertEquals(roleEntity.getId(), result.getId());
            assertEquals(roleEntity.getName(), result.getName());
        }
        @Test
        @DisplayName("Should throw RuntimeException when passed null Role")
        void whenPassedNullRole_shouldThrowRuntimeException() {
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> UtilService.verifyAndGetEntity(
                            null,
                            Role.class,
                            RoleDto.class,
                            roleMapper,
                            ERROR_MESSAGE
                    )
            );
            assertEquals("Object should not be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw TypeNotMatchException for invalid type in Role conversion")
        void whenPassedInvalidTypeForRole_shouldThrowTypeNotMatchException() {
            String invalidObject = "Invalid Object";

            TypeNotMatchException exception = assertThrows(
                    TypeNotMatchException.class,
                    () -> UtilService.verifyAndGetEntity(
                            invalidObject,
                            Role.class,
                            RoleDto.class,
                            roleMapper,
                            ERROR_MESSAGE
                    )
            );
            assertEquals(FULL_ERROR, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("StakeHolder Entity Conversion Tests")
    class StakeHolderConversionTests{
        @Mock
        private StakeHolderMapper stakeHolderMapper;

        private final String holderName = "John Doe";
        private final Long holderId = 1L;
        private final Long holderRoleId = 2L;
        private Role holderRole;
        private StakeHolder stakeHolderEntity;
        private StakeHolderDto stakeHolderDto;

        @BeforeEach
        void setUp() {
            holderRole = new Role("Investor", "Investment role", 3);
            holderRole.setId(holderRoleId);

            stakeHolderEntity = new StakeHolder(holderName, holderRole);
            stakeHolderEntity.setId(holderId);

            stakeHolderDto = new StakeHolderDto(holderId, holderName, holderRoleId);
        }

        @Test
        @DisplayName("Should return same StakeHolder entity when passed a StakeHolder")
        void whenPassedStakeHolderEntity_shouldReturnSameEntity() {
            StakeHolder result = UtilService.verifyAndGetEntity(
                    stakeHolderEntity,
                    StakeHolder.class,
                    StakeHolderDto.class,
                    stakeHolderMapper,
                    ERROR_MESSAGE
            );

            assertNotNull(result);
            assertEquals(stakeHolderEntity.getId(), result.getId());
            assertEquals(stakeHolderEntity.getName(), result.getName());
            assertEquals(stakeHolderEntity.getRole(), result.getRole());
        }

        @Test
        @DisplayName("Should convert StakeHolderDto to StakeHolder entity")
        void whenPassedStakeHolderDto_shouldConvertToEntity() {
            when(stakeHolderMapper.toEntity(any(StakeHolderDto.class))).thenReturn(stakeHolderEntity);

            StakeHolder result = UtilService.verifyAndGetEntity(
                    stakeHolderDto,
                    StakeHolder.class,
                    StakeHolderDto.class,
                    stakeHolderMapper,
                    ERROR_MESSAGE
            );

            assertNotNull(result);
            assertEquals(stakeHolderDto.getName(), result.getName());
            assertEquals(stakeHolderDto.getRoleId(), result.getRole().getId());
        }

        @Test
        @DisplayName("Should throw RuntimeException when passed null StakeHolder")
        void whenPassedNullStakeHolder_shouldThrowRuntimeException() {
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> UtilService.verifyAndGetEntity(
                            null,
                            StakeHolder.class,
                            StakeHolderDto.class,
                            stakeHolderMapper,
                            ERROR_MESSAGE
                    )
            );
            assertEquals("Object should not be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw TypeNotMatchException for invalid type in StakeHolder conversion")
        void whenPassedInvalidTypeForStakeHolder_shouldThrowTypeNotMatchException() {
            String invalidObject = "Invalid Object";

            TypeNotMatchException exception = assertThrows(
                    TypeNotMatchException.class,
                    () -> UtilService.verifyAndGetEntity(
                            invalidObject,
                            StakeHolder.class,
                            StakeHolderDto.class,
                            stakeHolderMapper,
                            ERROR_MESSAGE
                    )
            );
            assertEquals(FULL_ERROR, exception.getMessage());
        }
    }
}
