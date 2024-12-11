package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderLeafDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.StakeHolderLeafMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class StakeHolderLeafMapperTest {
    private final StakeHolderLeafMapper stakeHolderLeafMapper = StakeHolderLeafMapper.INSTANCE;
    private StakeHolder testStakeHolder;
    private StakeHolderLeaf testLeaf;
    private StakeHolderLeafDto testLeafDto;

    @BeforeEach
    void setUp() {
        // Set up test role
        Role role = new Role("Test Role", "Test Description", 1);
        role.setId(1L);

        // Set up test stakeholder
        testStakeHolder = new StakeHolder("Test StakeHolder", role);
        testStakeHolder.setId(1L);

        // Set up test leaf
        testLeaf = new StakeHolderLeaf(testStakeHolder);
        testLeaf.setId(1L);

        // Set up test DTO
        testLeafDto = new StakeHolderLeafDto(1L, 1L);
    }

    @Test
    void shouldMapStakeHolderLeafToDto() {
        StakeHolderLeafDto leafDto = stakeHolderLeafMapper.toDto(testLeaf);

        assertNotNull(leafDto);
        assertEquals(testLeaf.getId(), leafDto.getId());
        assertEquals(testStakeHolder.getId(), leafDto.getStakeholderId());
    }

    @Test
    void shouldMapDtoToStakeHolderLeaf() {
        StakeHolderLeaf leaf = stakeHolderLeafMapper.toEntity(testLeafDto);

        assertNotNull(leaf);
        assertEquals(testLeafDto.getId(), leaf.getId());
        assertEquals(testLeafDto.getStakeholderId(), leaf.getStakeHolder().getId());
    }

    @Test
    void shouldMapStakeHolderLeafListToDtoList() {
        StakeHolderLeaf leaf2 = new StakeHolderLeaf(testStakeHolder);
        leaf2.setId(2L);

        List<StakeHolderLeaf> leaves = Arrays.asList(testLeaf, leaf2);
        List<StakeHolderLeafDto> leafDtos = stakeHolderLeafMapper.toDtoList(leaves);

        assertNotNull(leafDtos);
        assertEquals(2, leafDtos.size());
        assertEquals(testLeaf.getId(), leafDtos.get(0).getId());
        assertEquals(leaf2.getId(), leafDtos.get(1).getId());
    }
}
