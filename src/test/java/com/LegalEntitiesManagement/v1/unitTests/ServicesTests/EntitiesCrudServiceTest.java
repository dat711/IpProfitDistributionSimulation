package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.*;
import com.LegalEntitiesManagement.v1.Entities.exceptions.*;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.*;
import com.LegalEntitiesManagement.v1.Entities.services.GraphBuilderService;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EntitiesCrudServiceTest extends BaseServiceTestMockedDependencies {
    @Mock
    private RoleService roleService;

    @Mock
    private StakeHolderService stakeHolderService;

    @Mock
    private IntellectualPropertyService intellectualPropertyService;

    @Mock
    private BaseContractParticipantService baseContractParticipantService;

    @Mock
    private StakeHolderLeafService stakeHolderLeafService;

    @Mock
    private BaseContractService baseContractService;

    @Mock
    private IpBasedContractService ipBasedContractService;

    @Mock
    private IpTreeService ipTreeService;

    @Mock
    private ContractNodeService contractNodeService;

    @Mock
    private ResponsibilityService responsibilityService;

    private EntitiesCrudService entitiesCrudService;

    private GraphBuilderService graphBuilderService;

    // Test data
    private Role testRole;
    private RoleDto testRoleDto;
    private StakeHolder testStakeHolder;
    private StakeHolderDto testStakeHolderDto;
    private IntellectualProperty testIp;
    private IntellectualPropertyDto testIpDto;
    private Contract testContract;
    private ContractDto testContractDto;
    private ContractParticipant testParticipant;
    private ParticipantDto testParticipantDto;
    private IpBasedContract testIpBasedContract;
    private IpBasedContractDto testIpBasedContractDto;
    private Set<ContractParticipant> testParticipants;
    private Set<ParticipantDto> testParticipantDtos;

    @BeforeEach
    void setUp() {
        super.baseSetUp();

        graphBuilderService = spy(new GraphBuilderService(
                ipTreeService,
                contractNodeService,
                stakeHolderLeafService,
                responsibilityService
        ));


        entitiesCrudService = new EntitiesCrudService(
                intellectualPropertyService,
                stakeHolderService,
                roleService,
                baseContractParticipantService,
                stakeHolderLeafService,
                baseContractService,
                graphBuilderService,
                ipBasedContractService
        );

        setupTestData();
    }

    private void setupTestData() {
        // Role
        testRole = new Role("Test Role", "Test Description", 1);
        testRole.setId(1L);
        testRoleDto = new RoleDto(1L, "Test Role", "Test Description", 1);

        // StakeHolder
        testStakeHolder = new StakeHolder("Test StakeHolder", testRole);
        testStakeHolder.setId(1L);
        testStakeHolderDto = new StakeHolderDto(1L, "Test StakeHolder", 1L);

        // IntellectualProperty
        testIp = new IntellectualProperty("Test IP", "Test IP Description");
        testIp.setId(1L);
        testIpDto = new IntellectualPropertyDto(1L, "Test IP", "Test IP Description");

        // Contract and IpBasedContract
        testContract = new Contract("Test Contract", LocalDate.now(), 1, testStakeHolder);
        testContract.setId(1L);
        testContractDto = new ContractDto(1L, "Test Contract", LocalDate.now(), 1, 1L);

        testIpBasedContract = new IpBasedContract("Test IP Contract", LocalDate.now(), 1, testIp, testStakeHolder);
        testIpBasedContract.setId(2L);
        testIpBasedContractDto = new IpBasedContractDto(2L, "Test IP Contract", LocalDate.now(), 1, 1L, 1L);

        // Participants
        testParticipant = new ContractParticipant(testContract, 50.0, true, testStakeHolder);
        testParticipant.setId(1L);
        testParticipantDto = new ParticipantDto( 1L, 50.0, true);

        testParticipants = new HashSet<>(Collections.singletonList(testParticipant));
        testParticipantDtos = new HashSet<>(Collections.singletonList(testParticipantDto));
    }

    @Nested
    @DisplayName("Role Operations Tests")
    class RoleOperationsTests {
        @Test
        @DisplayName("Should add new role successfully")
        void addRole_Success() {
            when(roleService.saveFromDto(any(RoleDto.class))).thenReturn(testRole);

            RoleDto result = entitiesCrudService.addRole(testRoleDto);

            assertNotNull(result);
            assertEquals(testRoleDto.getId(), result.getId());
            assertEquals(testRoleDto.getName(), result.getName());
            verify(roleService).saveFromDto(any(RoleDto.class));
        }

        @Test
        @DisplayName("Should get role by ID successfully")
        void getRole_Success() {
            when(roleService.findById(1L)).thenReturn(testRole);

            RoleDto result = entitiesCrudService.getRole(1L);

            assertNotNull(result);
            assertEquals(testRoleDto.getId(), result.getId());
            verify(roleService).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when role not found")
        void getRole_NotFound() {
            when(roleService.findById(99L)).thenThrow(new RoleNotFoundException(99L));

            assertThrows(RoleNotFoundException.class, () -> entitiesCrudService.getRole(99L));
            verify(roleService).findById(99L);
        }
    }

    @Nested
    @DisplayName("StakeHolder Operations Tests")
    class StakeHolderOperationsTests {
        @Test
        @DisplayName("Should add new stakeholder successfully")
        void addStakeHolder_Success() {
            when(roleService.findById(1L)).thenReturn(testRole);
            when(stakeHolderService.saveFromDto(any(StakeHolderDto.class))).thenReturn(testStakeHolder);
            when(stakeHolderLeafService.save(any(StakeHolderLeaf.class))).thenReturn(new StakeHolderLeaf(testStakeHolder));

            StakeHolderDto result = entitiesCrudService.addStakeHolder(testStakeHolderDto);

            assertNotNull(result);
            assertEquals(testStakeHolderDto.getId(), result.getId());
            verify(stakeHolderService).saveFromDto(any(StakeHolderDto.class));
            verify(stakeHolderLeafService).save(any(StakeHolderLeaf.class));
        }

        @Test
        @DisplayName("Should update stakeholder successfully")
        void updateStakeHolder_Success() {
            when(stakeHolderService.updateFromDto(any(StakeHolderDto.class))).thenReturn(testStakeHolder);

            StakeHolderDto result = entitiesCrudService.updateStakeHolder(testStakeHolderDto);

            assertNotNull(result);
            assertEquals(testStakeHolderDto.getId(), result.getId());
            verify(stakeHolderService).updateFromDto(testStakeHolderDto);
        }
    }

    @Nested
    @DisplayName("IntellectualProperty Operations Tests")
    class IntellectualPropertyOperationsTests {
        @Test
        @DisplayName("Should add new IP successfully")
        void addIntellectualProperty_Success() {
            when(intellectualPropertyService.saveFromDto(any(IntellectualPropertyDto.class))).thenReturn(testIp);

            IntellectualPropertyDto result = entitiesCrudService.addIntellectualProperty(testIpDto);

            assertNotNull(result);
            assertEquals(testIpDto.getId(), result.getId());
            verify(intellectualPropertyService).saveFromDto(testIpDto);
        }

        @Test
        @DisplayName("Should update IP successfully")
        void updateIntellectualProperty_Success() {
            doNothing().when(intellectualPropertyService).verify(testIpDto);
            when(intellectualPropertyService.updateFromDto(any(IntellectualPropertyDto.class))).thenReturn(testIp);
            IntellectualPropertyDto result = entitiesCrudService.updateIntellectualProperty(testIpDto);

            assertNotNull(result);
            assertEquals(testIpDto.getId(), result.getId());
            verify(intellectualPropertyService).updateFromDto(testIpDto);
        }
    }

    @Nested
    @DisplayName("Contract Operations Tests")
    class ContractOperationsTests {
        @Test
        @DisplayName("Should save contract composition successfully")
        void saveContract_Success() {
            ContractCompositionDto compositionDto = new ContractCompositionDto(testContractDto, testParticipantDtos);

            when(baseContractService.saveFromDto(any(ContractDto.class))).thenReturn(testContract);
            when(baseContractParticipantService.saveAll(anyList())).thenReturn(new ArrayList<>(testParticipants));

            ContractCompositionDto result = entitiesCrudService.saveContract(compositionDto);

            assertNotNull(result);
            assertEquals(testContractDto.getId(), result.getContractDto().getId());
            assertFalse(result.getParticipants().isEmpty());
            verify(baseContractService).saveFromDto(any(ContractDto.class));
            verify(baseContractParticipantService).saveAll(anyList());
        }

        @Test
        @DisplayName("Should save IP-based contract successfully")
        void saveIpBasedContract_Success() {
            IpBasedContractCompositionDto compositionDto =
                    new IpBasedContractCompositionDto(testIpBasedContractDto, testParticipantDtos);

            when(intellectualPropertyService.existsById(anyLong())).thenReturn(true);
            when(ipBasedContractService.saveFromDto(any(IpBasedContractDto.class))).thenReturn(testIpBasedContract);
            when(baseContractParticipantService.saveAll(anyList())).thenReturn(new ArrayList<>(testParticipants));
            when(ipTreeService.existsById(anyLong())).thenReturn(false);
            doNothing().when(graphBuilderService).validateBuildNewTree(any());
            doReturn(new IpTree()).when(graphBuilderService).buildNewTree(any());
            IpBasedContractCompositionDto result = entitiesCrudService.saveIpBasedContract(compositionDto);

            assertNotNull(result);
            assertEquals(testIpBasedContractDto.getId(), result.getContractDto().getId());
            assertFalse(result.getParticipants().isEmpty());
            verify(ipBasedContractService).saveFromDto(any(IpBasedContractDto.class));
            verify(baseContractParticipantService).saveAll(anyList());
        }
    }
}
