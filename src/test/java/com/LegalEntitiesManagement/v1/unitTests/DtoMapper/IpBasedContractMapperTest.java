package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;

import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IpBasedContractMapper;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IpBasedContractMapperTest {
    private final IpBasedContractMapper ipBasedContractMapper = IpBasedContractMapper.INSTANCE;

    @Test
    void shouldMapIpBasedContractToDto() {
        // given
        StakeHolder executor = new StakeHolder();
        executor.setId(1L);

        IntellectualProperty ip = new IntellectualProperty();
        ip.setId(1L);
        ip.setName("Test IP");

        IpBasedContract contract = new IpBasedContract();
        contract.setId(1L);
        contract.setDescription("Test IP Contract");
        contract.setContractActiveDate(LocalDate.now());
        contract.setContractPriority(1);
        contract.setExecutor(executor);
        contract.setIntellectualProperty(ip);

        // when
        IpBasedContractDto dto = ipBasedContractMapper.toDto(contract);

        // then
        assertNotNull(dto);
        assertEquals(contract.getId(), dto.getId());
        assertEquals(contract.getDescription(), dto.getDescription());
        assertEquals(contract.getContractActiveDate(), dto.getContractActiveDate());
        assertEquals(contract.getContractPriority(), dto.getContractPriority());
        assertEquals(executor.getId(), dto.getExecutorId());
        assertEquals(ip.getId(), dto.getIpId());
    }

    @Test
    void shouldMapDtoToIpBasedContract() {
        // given
        IpBasedContractDto dto = new IpBasedContractDto();
        dto.setId(1L);
        dto.setDescription("Test IP Contract");
        dto.setContractActiveDate(LocalDate.now());
        dto.setContractPriority(1);
        dto.setExecutorId(1L);
        dto.setIpId(1L);

        // when
        IpBasedContract contract = ipBasedContractMapper.toEntity(dto);

        // then
        assertNotNull(contract);
        assertEquals(dto.getId(), contract.getId());
        assertEquals(dto.getDescription(), contract.getDescription());
        assertEquals(dto.getContractActiveDate(), contract.getContractActiveDate());
        assertEquals(dto.getContractPriority(), contract.getContractPriority());
        assertEquals(dto.getExecutorId(), contract.getExecutor().getId());
        assertEquals(dto.getIpId(), contract.getIntellectualProperty().getId());
    }

    @Test
    void shouldMapIpBasedContractListToDtoList() {
        // given
        StakeHolder executor = new StakeHolder();
        executor.setId(1L);

        IntellectualProperty ip = new IntellectualProperty();
        ip.setId(1L);

        IpBasedContract contract1 = new IpBasedContract();
        contract1.setId(1L);
        contract1.setDescription("Test IP Contract 1");
        contract1.setExecutor(executor);
        contract1.setIntellectualProperty(ip);

        IpBasedContract contract2 = new IpBasedContract();
        contract2.setId(2L);
        contract2.setDescription("Test IP Contract 2");
        contract2.setExecutor(executor);
        contract2.setIntellectualProperty(ip);

        List<IpBasedContract> contracts = Arrays.asList(contract1, contract2);

        // when
        List<IpBasedContractDto> dtos = ipBasedContractMapper.toDtoList(contracts);

        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(contract1.getDescription(), dtos.get(0).getDescription());
        assertEquals(contract2.getDescription(), dtos.get(1).getDescription());
        assertEquals(ip.getId(), dtos.get(0).getIpId());
        assertEquals(ip.getId(), dtos.get(1).getIpId());
    }

    @Test
    void shouldMapDtoListToIpBasedContractList() {
        // given
        LocalDate now = LocalDate.now();
        IpBasedContractDto dto1 = new IpBasedContractDto(1L, "Test IP Contract 1", now, 1, 1L, 1L);
        IpBasedContractDto dto2 = new IpBasedContractDto(2L, "Test IP Contract 2", now, 1, 1L, 1L);
        List<IpBasedContractDto> dtos = Arrays.asList(dto1, dto2);

        // when
        List<IpBasedContract> contracts = ipBasedContractMapper.toEntityList(dtos);

        // then
        assertNotNull(contracts);
        assertEquals(2, contracts.size());
        assertEquals(dto1.getDescription(), contracts.get(0).getDescription());
        assertEquals(dto2.getDescription(), contracts.get(1).getDescription());
        assertEquals(dto1.getIpId(), contracts.get(0).getIntellectualProperty().getId());
        assertEquals(dto2.getIpId(), contracts.get(1).getIntellectualProperty().getId());
    }
}
