package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;
import com.LegalEntitiesManagement.v1.Entities.dto.ResponsibilityDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ResponsibilityMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.MoneyNode;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ResponsibilityMapperTest {
    private final ResponsibilityMapper responsibilitiesMapper = ResponsibilityMapper.INSTANCE;
    private Responsibility responsibility;
    private ResponsibilityDto responsibilityDto;
    private MoneyNode sourceNode;
    private MoneyNode targetNode;

    @BeforeEach
    void setUp() {
        // Setup source and target nodes
        Contract contract = new Contract();
        contract.setId(1L);
        sourceNode = new ContractNode(contract);
        sourceNode.setId(1L);

        StakeHolder stakeHolder = new StakeHolder();
        stakeHolder.setId(1L);
        targetNode = new StakeHolderLeaf(stakeHolder);
        targetNode.setId(2L);

        // Setup Responsibilities
        responsibility = new Responsibility();
        responsibility.setId(1L);
        responsibility.setSource(sourceNode);
        responsibility.setTarget(targetNode);
        responsibility.setPercentage(25.0);

        // Setup DTO
        responsibilityDto = new ResponsibilityDto(1L, 2L, 1L, 25.0);
    }

    @Test
    @DisplayName("Should map Responsibility to DTO")
    void shouldMapResponsibilitiesToDto() {
        // when
        ResponsibilityDto dto = responsibilitiesMapper.toDto(responsibility);

        // then
        assertNotNull(dto);
        assertEquals(responsibility.getId(), dto.getId());
        assertEquals(responsibility.getSource().getId(), dto.getSourceNodeId());
        assertEquals(responsibility.getTarget().getId(), dto.getTargetNodeId());
        assertEquals(responsibility.getPercentage(), dto.getPercentage());
    }

    @Test
    @DisplayName("Should map DTO to Responsibilities")
    void shouldMapDtoToResponsibility() {
        // when
        Responsibility entity = responsibilitiesMapper.toEntity(responsibilityDto);

        // then
        assertNotNull(entity);
        assertEquals(responsibilityDto.getId(), entity.getId());
        assertEquals(responsibilityDto.getSourceNodeId(), entity.getSource().getId());
        assertEquals(responsibilityDto.getTargetNodeId(), entity.getTarget().getId());
        assertEquals(responsibilityDto.getPercentage(), entity.getPercentage());
    }

    @Test
    @DisplayName("Should map Responsibility list to DTO list")
    void shouldMapResponsibilitiesListToDtoList() {
        // given
        Responsibility responsibilities2 = new Responsibility();
        responsibilities2.setId(2L);
        responsibilities2.setSource(sourceNode);
        responsibilities2.setTarget(targetNode);
        responsibilities2.setPercentage(75.0);
        List<Responsibility> responsibilitiesList = Arrays.asList(responsibility, responsibilities2);

        // when
        List<ResponsibilityDto> dtos = responsibilitiesMapper.toDtoList(responsibilitiesList);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(responsibilitiesList.get(0).getId(), dtos.get(0).getId());
        assertEquals(responsibilitiesList.get(1).getId(), dtos.get(1).getId());
        assertEquals(responsibilitiesList.get(0).getPercentage(), dtos.get(0).getPercentage());
        assertEquals(responsibilitiesList.get(1).getPercentage(), dtos.get(1).getPercentage());
        assertEquals(responsibilitiesList.get(0).getSource().getId(), dtos.get(0).getSourceNodeId());
        assertEquals(responsibilitiesList.get(0).getTarget().getId(), dtos.get(0).getTargetNodeId());
    }

    @Test
    @DisplayName("Should map DTO list to Responsibility list")
    void shouldMapDtoListToResponsibilitiesList() {
        // given
        ResponsibilityDto dto2 = new ResponsibilityDto(2L, 2L, 1L, 75.0);
        List<ResponsibilityDto> dtos = Arrays.asList(responsibilityDto, dto2);

        // when
        List<Responsibility> entities = responsibilitiesMapper.toEntityList(dtos);

        // then
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(dtos.get(0).getId(), entities.get(0).getId());
        assertEquals(dtos.get(1).getId(), entities.get(1).getId());
        assertEquals(dtos.get(0).getPercentage(), entities.get(0).getPercentage());
        assertEquals(dtos.get(1).getPercentage(), entities.get(1).getPercentage());
        assertEquals(dtos.get(0).getSourceNodeId(), entities.get(0).getSource().getId());
        assertEquals(dtos.get(0).getTargetNodeId(), entities.get(0).getTarget().getId());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void shouldHandleNullValues() {
        // given
        responsibility.setSource(null);
        responsibility.setTarget(null);

        // when
        ResponsibilityDto dto = responsibilitiesMapper.toDto(responsibility);

        // then
        assertNotNull(dto);
        assertEquals(responsibility.getId(), dto.getId());
        assertEquals(responsibility.getPercentage(), dto.getPercentage());
        assertNull(dto.getSourceNodeId());
        assertNull(dto.getTargetNodeId());
    }

    @Test
    @DisplayName("Should map non-null values when creating entity from DTO")
    void shouldMapNonNullValuesInDtoToEntity() {
        // given
        responsibilityDto.setPercentage(50.0);

        // when
        Responsibility entity = responsibilitiesMapper.toEntity(responsibilityDto);

        // then
        assertNotNull(entity);
        assertEquals(responsibilityDto.getId(), entity.getId());
        assertEquals(50.0, entity.getPercentage());
        assertNotNull(entity.getSource());
        assertNotNull(entity.getTarget());
    }
}
