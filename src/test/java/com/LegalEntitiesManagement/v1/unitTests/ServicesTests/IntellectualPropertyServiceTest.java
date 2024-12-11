package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.IntellectualPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IntellectualPropertyServiceTest extends BaseServiceTestMockedDependencies{
    private IntellectualPropertyService intellectualPropertyService;
    private IntellectualProperty testIp;
    private IntellectualPropertyDto testIpDto;

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        intellectualPropertyService = new IntellectualPropertyService(intellectualPropertyRepository);

        // Create test data
        testIp = new IntellectualProperty("Sunday Morning", "Pop Song of Maroon 5");
        testIp.setId(1L);

        testIpDto = new IntellectualPropertyDto();
        testIpDto.setId(1L);
        testIpDto.setName("Sunday Morning");
        testIpDto.setDescription("Pop Song of Maroon 5");
    }

    @Test
    void findById_ExistingId_ReturnsIntellectualProperty() {
        when(intellectualPropertyRepository.findById(1L)).thenReturn(Optional.of(testIp));

        IntellectualProperty foundIp = intellectualPropertyService.findById(1L);

        assertNotNull(foundIp);
        assertEquals(testIp.getId(), foundIp.getId());
        assertEquals(testIp.getName(), foundIp.getName());
        verify(intellectualPropertyRepository).findById(1L);
    }

    @Test
    void findById_NonExistingId_ThrowsException() {
        when(intellectualPropertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IpNotFoundException.class, () -> intellectualPropertyService.findById(99L));
        verify(intellectualPropertyRepository).findById(99L);
    }

    @Test
    void save_ValidIp_ReturnsSavedIp() {
        when(intellectualPropertyRepository.save(any(IntellectualProperty.class))).thenReturn(testIp);

        IntellectualProperty savedIp = intellectualPropertyService.save(testIp);

        assertNotNull(savedIp);
        assertEquals(testIp.getName(), savedIp.getName());
        verify(intellectualPropertyRepository).save(testIp);
    }

    @Test
    void saveFromDto_ValidDto_ReturnsSavedIp() {
        when(intellectualPropertyRepository.save(any(IntellectualProperty.class))).thenReturn(testIp);

        IntellectualProperty savedIp = intellectualPropertyService.saveFromDto(testIpDto);

        assertNotNull(savedIp);
        assertEquals(testIp.getName(), savedIp.getName());
        verify(intellectualPropertyRepository).save(any(IntellectualProperty.class));
    }

    @Test
    void findAll_ReturnsAllIps() {
        IntellectualProperty secondIp = new IntellectualProperty("Numb", "Rock Song of Linkin Park");
        secondIp.setId(2L);
        List<IntellectualProperty> ips = Arrays.asList(testIp, secondIp);
        when(intellectualPropertyRepository.findAll()).thenReturn(ips);

        List<IntellectualProperty> foundIps = intellectualPropertyService.findAll();

        assertNotNull(foundIps);
        assertEquals(2, foundIps.size());
        verify(intellectualPropertyRepository).findAll();
    }

    @Test
    void verify_ValidIp_NoException() {
        when(intellectualPropertyRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> intellectualPropertyService.verify(testIp));
        verify(intellectualPropertyRepository).existsById(1L);
    }

    @Test
    void verify_ValidDto_NoException() {
        when(intellectualPropertyRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> intellectualPropertyService.verify(testIpDto));
        verify(intellectualPropertyRepository).existsById(1L);
    }

    @Test
    void verify_NonExistingIp_ThrowsException() {
        when(intellectualPropertyRepository.existsById(1L)).thenReturn(false);

        assertThrows(IpNotFoundException.class, () -> intellectualPropertyService.verify(testIp));
        verify(intellectualPropertyRepository).existsById(1L);
    }

    @Test
    void updateFromDto_ValidDto_ReturnsUpdatedIp() {
        when(intellectualPropertyRepository.save(any(IntellectualProperty.class))).thenReturn(testIp);

        IntellectualProperty updatedIp = intellectualPropertyService.updateFromDto(testIpDto);

        assertNotNull(updatedIp);
        assertEquals(testIpDto.getName(), updatedIp.getName());
        verify(intellectualPropertyRepository).save(any(IntellectualProperty.class));
    }

    @Test
    void verify_NullObject_ThrowsRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> intellectualPropertyService.verify(null));
        assertEquals("Object should not be null", exception.getMessage());
    }

    @Test
    void verify_InvalidTypeString_ThrowsTypeNotMatchException() {
        String invalidObject = "Invalid type";
        TypeNotMatchException exception = assertThrows(TypeNotMatchException.class,
                () -> intellectualPropertyService.verify(invalidObject));
        assertEquals("The Type is not match system requirements with details: The object must belong to either the IntellectualProperty or IntellectualPropertyDto class",
                exception.getMessage());
    }

}
