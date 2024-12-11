package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ContractMapper;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContractMapperTest {
    private final ContractMapper contractMapper = ContractMapper.INSTANCE;

    @Test
    void shouldMapContractToDto() {
        // given
        StakeHolder executor = new StakeHolder();
        executor.setId(1L);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setDescription("Test Contract");
        contract.setContractActiveDate(LocalDate.now());
        contract.setContractPriority(1);
        contract.setExecutor(executor);

        // when
        ContractDto dto = contractMapper.toDto(contract);

        // then
        assertNotNull(dto);
        assertEquals(contract.getId(), dto.getId());
        assertEquals(contract.getDescription(), dto.getDescription());
        assertEquals(contract.getContractActiveDate(), dto.getContractActiveDate());
        assertEquals(contract.getContractPriority(), dto.getContractPriority());
        assertEquals(executor.getId(), dto.getExecutorId());
    }

    @Test
    void shouldMapDtoToContract() {
        // given
        ContractDto dto = new ContractDto();
        dto.setId(1L);
        dto.setDescription("Test Contract");
        dto.setContractActiveDate(LocalDate.now());
        dto.setContractPriority(1);
        dto.setExecutorId(1L);

        // when
        Contract contract = contractMapper.toEntity(dto);

        // then
        assertNotNull(contract);
        assertEquals(dto.getId(), contract.getId());
        assertEquals(dto.getDescription(), contract.getDescription());
        assertEquals(dto.getContractActiveDate(), contract.getContractActiveDate());
        assertEquals(dto.getContractPriority(), contract.getContractPriority());
        assertEquals(dto.getExecutorId(), contract.getExecutor().getId());
    }

    @Test
    void shouldMapContractListToDtoList() {
        // given
        StakeHolder executor = new StakeHolder();
        executor.setId(1L);

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setDescription("Test Contract 1");
        contract1.setExecutor(executor);

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setDescription("Test Contract 2");
        contract2.setExecutor(executor);

        List<Contract> contracts = Arrays.asList(contract1, contract2);

        // when
        List<ContractDto> dtos = contractMapper.toDtoList(contracts);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(contract1.getDescription(), dtos.get(0).getDescription());
        assertEquals(contract2.getDescription(), dtos.get(1).getDescription());
    }

    @Test
    void shouldMapDtoListToContractList() {
        // given
        ContractDto dto1 = new ContractDto(1L, "Test Contract 1", LocalDate.now(), 1, 1L);
        ContractDto dto2 = new ContractDto(2L, "Test Contract 2", LocalDate.now(), 1, 1L);
        List<ContractDto> dtos = Arrays.asList(dto1, dto2);

        // when
        List<Contract> contracts = contractMapper.toEntityList(dtos);

        // then
        assertNotNull(contracts);
        assertEquals(2, contracts.size());
        assertEquals(dto1.getDescription(), contracts.get(0).getDescription());
        assertEquals(dto2.getDescription(), contracts.get(1).getDescription());
    }
}
