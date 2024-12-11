package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class StakeHolderMapperTest {
    private final StakeHolderMapper stakeHolderMapper = StakeHolderMapper.INSTANCE;

    private final String RoleName = "Publisher";
    private final String RoleDescription = "The institution that hold the right to distribute Intellectual property";

    private final int RolePriority = 3;

    private Role GetRole(){
        Role role = new Role(RoleName,RoleDescription, RolePriority );
        role.setId(1L);
        return role;
    }

    @Test
    public void MapStakeHolderToStakeHolderDto(){
        Role role = GetRole();
        StakeHolder stakeHolder = new StakeHolder("Warner Music", role);
        stakeHolder.setId(1L);

        // when
        StakeHolderDto stakeHolderDto = stakeHolderMapper.toDto(stakeHolder);

        // then
        assertNotNull(stakeHolderDto);
        assertEquals(stakeHolder.getId(), stakeHolderDto.getId());
        assertEquals(stakeHolder.getName(), stakeHolderDto.getName());
        assertEquals(stakeHolder.getRole().getId(), stakeHolderDto.getRoleId());
    }

    @Test
    void shouldMapStakeHolderDtoToStakeHolder() {
        // given
        StakeHolderDto stakeHolderDto = new StakeHolderDto(1L, "Warner Music", 1L);

        // when
        StakeHolder stakeHolder = stakeHolderMapper.toEntity(stakeHolderDto);

        // then
        assertNotNull(stakeHolder);
        assertEquals(stakeHolderDto.getId(), stakeHolder.getId());
        assertEquals(stakeHolderDto.getName(), stakeHolder.getName());
        assertEquals(stakeHolderDto.getRoleId(), stakeHolder.getRole().getId());
    }

    @Test
    void shouldMapStakeHolderListToStakeHolderDtoList() {
        // given
        Role role1 = new Role("Publisher", "Music Publisher", RolePriority);
        role1.setId(1L);
        Role role2 = new Role("Artist", "Music Artist", RolePriority);
        role2.setId(2L);

        StakeHolder stakeHolder1 = new StakeHolder("Warner Music", role1);
        stakeHolder1.setId(1L);
        StakeHolder stakeHolder2 = new StakeHolder("Taylor Swift", role2);
        stakeHolder2.setId(2L);

        List<StakeHolder> stakeHolders = Arrays.asList(stakeHolder1, stakeHolder2);

        // when
        List<StakeHolderDto> stakeHolderDtos = stakeHolderMapper.toDtoList(stakeHolders);

        // then
        assertNotNull(stakeHolderDtos);
        assertEquals(2, stakeHolderDtos.size());
        assertEquals(stakeHolder1.getId(), stakeHolderDtos.get(0).getId());
        assertEquals(stakeHolder1.getName(), stakeHolderDtos.get(0).getName());
        assertEquals(stakeHolder1.getRole().getId(), stakeHolderDtos.get(0).getRoleId());
        assertEquals(stakeHolder2.getId(), stakeHolderDtos.get(1).getId());
        assertEquals(stakeHolder2.getName(), stakeHolderDtos.get(1).getName());
        assertEquals(stakeHolder2.getRole().getId(), stakeHolderDtos.get(1).getRoleId());
    }

    @Test
    void shouldMapStakeHolderDtoListToStakeHolderList() {
        // given
        StakeHolderDto dto1 = new StakeHolderDto(1L, "Warner Music", 1L);
        StakeHolderDto dto2 = new StakeHolderDto(2L, "Taylor Swift", 2L);
        List<StakeHolderDto> dtos = Arrays.asList(dto1, dto2);

        // when
        List<StakeHolder> stakeHolders = stakeHolderMapper.toEntityList(dtos);

        // then
        assertNotNull(stakeHolders);
        assertEquals(2, stakeHolders.size());
        assertEquals(dto1.getId(), stakeHolders.get(0).getId());
        assertEquals(dto1.getName(), stakeHolders.get(0).getName());
        assertEquals(dto1.getRoleId(), stakeHolders.get(0).getRole().getId());
        assertEquals(dto2.getId(), stakeHolders.get(1).getId());
        assertEquals(dto2.getName(), stakeHolders.get(1).getName());
        assertEquals(dto2.getRoleId(), stakeHolders.get(1).getRole().getId());
    }
}
