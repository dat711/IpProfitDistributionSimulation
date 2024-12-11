package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.IpTreeDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IpTreeMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("IpTree Mapper Tests")
public class IpTreeMapperTest {
    private final IpTreeMapper ipTreeMapper = IpTreeMapper.INSTANCE;
    private IpTree ipTree;
    private IpTreeDto ipTreeDto;
    private IntellectualProperty ip;
    private ContractNode rootContractNode;

    @BeforeEach
    void setUp() {
        // Setup IP
        ip = new IntellectualProperty();
        ip.setId(1L);
        ip.setName("Test IP");

        // Setup Root ContractNode
        Contract contract = new Contract();
        contract.setId(1L);
        rootContractNode = new ContractNode(contract);
        rootContractNode.setId(1L);

        // Setup IpTree
        ipTree = new IpTree(ip, rootContractNode);
        ipTree.setId(1L);

        // Setup DTO
        ipTreeDto = new IpTreeDto(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should map IpTree to DTO")
    void shouldMapIpTreeToDto() {
        // when
        IpTreeDto dto = ipTreeMapper.toDto(ipTree);

        // then
        assertNotNull(dto);
        assertEquals(ipTree.getId(), dto.getId());
        assertEquals(ipTree.getIntellectualProperty().getId(), dto.getIntellectualPropertyId());
        assertEquals(ipTree.getRootContractNode().getId(), dto.getRootContractNodeId());
    }

    @Test
    @DisplayName("Should map DTO to IpTree")
    void shouldMapDtoToIpTree() {
        // when
        IpTree entity = ipTreeMapper.toEntity(ipTreeDto);

        // then
        assertNotNull(entity);
        assertEquals(ipTreeDto.getId(), entity.getId());
        assertEquals(ipTreeDto.getIntellectualPropertyId(), entity.getIntellectualProperty().getId());
        assertEquals(ipTreeDto.getRootContractNodeId(), entity.getRootContractNode().getId());
    }

    @Test
    @DisplayName("Should map IpTree list to DTO list")
    void shouldMapIpTreeListToDtoList() {
        // given
        IpTree ipTree2 = new IpTree();
        ipTree2.setId(2L);
        ipTree2.setIntellectualProperty(ip);
        ipTree2.setRootContractNode(rootContractNode);
        List<IpTree> ipTrees = Arrays.asList(ipTree, ipTree2);

        // when
        List<IpTreeDto> dtos = ipTreeMapper.toDtoList(ipTrees);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(ipTrees.get(0).getId(), dtos.get(0).getId());
        assertEquals(ipTrees.get(1).getId(), dtos.get(1).getId());
    }

    @Test
    @DisplayName("Should map DTO list to IpTree list")
    void shouldMapDtoListToIpTreeList() {
        // given
        IpTreeDto ipTreeDto2 = new IpTreeDto(2L, 1L, 1L);
        List<IpTreeDto> dtos = Arrays.asList(ipTreeDto, ipTreeDto2);

        // when
        List<IpTree> entities = ipTreeMapper.toEntityList(dtos);

        // then
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(dtos.get(0).getId(), entities.get(0).getId());
        assertEquals(dtos.get(1).getId(), entities.get(1).getId());
    }
}
