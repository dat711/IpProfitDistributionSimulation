package com.LegalEntitiesManagement.v1.unitTests.DtoMapper;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IntellectualPropertyMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntellectualPropertyMapperTest {
    private final IntellectualPropertyMapper intellectualPropertyMapper = IntellectualPropertyMapper.INSTANCE;

    @Test
    void shouldMapIntellectualPropertyToDto() {
        // given
        IntellectualProperty ip = new IntellectualProperty("Sunday Morning", "Pop Song of Maroon 5");
        ip.setId(1L);

        // when
        IntellectualPropertyDto ipDto = intellectualPropertyMapper.toDto(ip);

        // then
        assertNotNull(ipDto);
        assertEquals(ip.getId(), ipDto.getId());
        assertEquals(ip.getName(), ipDto.getName());
        assertEquals(ip.getDescription(), ipDto.getDescription());
    }

    @Test
    void shouldMapDtoToIntellectualProperty() {
        // given
        IntellectualPropertyDto ipDto = new IntellectualPropertyDto();
        ipDto.setId(1L);
        ipDto.setName("Sunday Morning");
        ipDto.setDescription("Pop Song of Maroon 5");

        // when
        IntellectualProperty ip = intellectualPropertyMapper.toEntity(ipDto);

        // then
        assertNotNull(ip);
        assertEquals(ipDto.getId(), ip.getId());
        assertEquals(ipDto.getName(), ip.getName());
        assertEquals(ipDto.getDescription(), ip.getDescription());
    }

    @Test
    void shouldMapIntellectualPropertyListToDtoList() {
        // given
        IntellectualProperty ip1 = new IntellectualProperty("Sunday Morning", "Pop Song of Maroon 5");
        IntellectualProperty ip2 = new IntellectualProperty("Numb", "Rock Song of Linkin Park");
        ip1.setId(1L);
        ip2.setId(2L);
        List<IntellectualProperty> ips = Arrays.asList(ip1, ip2);

        // when
        List<IntellectualPropertyDto> ipDtos = intellectualPropertyMapper.toDtoList(ips);

        // then
        assertNotNull(ipDtos);
        assertEquals(2, ipDtos.size());
        assertEquals(ip1.getName(), ipDtos.get(0).getName());
        assertEquals(ip2.getName(), ipDtos.get(1).getName());
    }

    @Test
    void shouldMapDtoListToIntellectualPropertyList() {
        // given
        IntellectualPropertyDto ipDto1 = new IntellectualPropertyDto(1L, "Sunday Morning", "Pop Song of Maroon 5");
        IntellectualPropertyDto ipDto2 = new IntellectualPropertyDto(2L, "Numb", "Rock Song of Linkin Park");
        List<IntellectualPropertyDto> ipDtos = Arrays.asList(ipDto1, ipDto2);

        // when
        List<IntellectualProperty> ips = intellectualPropertyMapper.toEntityList(ipDtos);

        // then
        assertNotNull(ips);
        assertEquals(2, ips.size());
        assertEquals(ipDto1.getName(), ips.get(0).getName());
        assertEquals(ipDto2.getName(), ips.get(1).getName());
    }
}
