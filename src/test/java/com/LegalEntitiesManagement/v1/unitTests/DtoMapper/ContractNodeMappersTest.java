package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractNodeDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractNodeMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ContractNodeMappersTest {
    @Nested
    @DisplayName("ContractNode Mapper Tests")
    class ContractNodeMapperTest {
        private final ContractNodeMapper contractNodeMapper = ContractNodeMapper.INSTANCE;
        private Contract contract;
        private ContractNode contractNode;
        private ContractNodeDto contractNodeDto;

        @BeforeEach
        void setUp() {
            // Create a base Contract
            contract = new Contract();
            contract.setId(1L);
            contract.setDescription("Test Contract");
            contract.setContractActiveDate(LocalDate.now());
            contract.setContractPriority(1);

            // Create a ContractNode
            contractNode = new ContractNode(contract);
            contractNode.setId(1L);

            // Create a ContractNodeDto
            contractNodeDto = new ContractNodeDto(1L, 1L);
        }

        @Test
        @DisplayName("Should map ContractNode to DTO")
        void shouldMapContractNodeToDto() {
            // when
            ContractNodeDto dto = contractNodeMapper.toDto(contractNode);

            // then
            assertNotNull(dto);
            assertEquals(contractNode.getId(), dto.getId());
            assertEquals(contractNode.getContract().getId(), dto.getContractId());
        }

        @Test
        @DisplayName("Should map DTO to ContractNode")
        void shouldMapDtoToContractNode() {
            // when
            ContractNode node = contractNodeMapper.toEntity(contractNodeDto);

            // then
            assertNotNull(node);
            assertEquals(contractNodeDto.getId(), node.getId());
            assertEquals(contractNodeDto.getContractId(), node.getContract().getId());
        }

        @Test
        @DisplayName("Should map ContractNode list to DTO list")
        void shouldMapContractNodeListToDtoList() {
            // given
            ContractNode node2 = new ContractNode();
            Contract contract2 = new Contract();
            contract2.setId(2L);
            node2.setId(2L);
            node2.setContract(contract2);
            List<ContractNode> nodes = Arrays.asList(contractNode, node2);

            // when
            List<ContractNodeDto> dtos = contractNodeMapper.toDtoList(nodes);

            // then
            assertNotNull(dtos);
            assertEquals(2, dtos.size());
            assertEquals(nodes.get(0).getId(), dtos.get(0).getId());
            assertEquals(nodes.get(1).getId(), dtos.get(1).getId());
            assertEquals(nodes.get(0).getContract().getId(), dtos.get(0).getContractId());
            assertEquals(nodes.get(1).getContract().getId(), dtos.get(1).getContractId());
        }

        @Test
        @DisplayName("Should map DTO list to ContractNode list")
        void shouldMapDtoListToContractNodeList() {
            // given
            ContractNodeDto dto2 = new ContractNodeDto(2L, 2L);
            List<ContractNodeDto> dtos = Arrays.asList(contractNodeDto, dto2);

            // when
            List<ContractNode> nodes = contractNodeMapper.toEntityList(dtos);

            // then
            assertNotNull(nodes);
            assertEquals(2, nodes.size());
            assertEquals(dtos.get(0).getId(), nodes.get(0).getId());
            assertEquals(dtos.get(1).getId(), nodes.get(1).getId());
            assertEquals(dtos.get(0).getContractId(), nodes.get(0).getContract().getId());
            assertEquals(dtos.get(1).getContractId(), nodes.get(1).getContract().getId());
        }
    }
}
