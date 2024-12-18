package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;

import com.LegalEntitiesManagement.v1.Entities.controllers.IntellectualPropertyController;
import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IntellectualPropertyController.class)
public class IntellectualPropertyControllerTest extends BaseControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private IntellectualPropertyDto validIpDto;
    private IntellectualPropertyDto invalidIpDto;

    @BeforeEach
    void setUp() {
        // Set up valid IP DTO
        validIpDto = new IntellectualPropertyDto();
        validIpDto.setId(1L);
        validIpDto.setName("Test IP");
        validIpDto.setDescription("Test IP Description");

        // Set up invalid IP DTO
        invalidIpDto = new IntellectualPropertyDto();
        invalidIpDto.setId(2L);
        invalidIpDto.setName("");  // Invalid: empty name
        invalidIpDto.setDescription("");  // Invalid: empty description
    }

    @Test
    void createIntellectualProperty_ValidInput_Success() throws Exception {
        when(entitiesCrudService.addIntellectualProperty(any(IntellectualPropertyDto.class))).thenReturn(validIpDto);
        mockMvc.perform(post("/api/v1/intellectual-properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIpDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(validIpDto.getId()))
                .andExpect(jsonPath("$.data.name").value(validIpDto.getName()))
                .andExpect(jsonPath("$.data.description").value(validIpDto.getDescription()));

        verify(entitiesCrudService).addIntellectualProperty(any(IntellectualPropertyDto.class));
    }

    @Test
    void createIntellectualProperty_InvalidInput_BadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/intellectual-properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIpDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).addIntellectualProperty(any(IntellectualPropertyDto.class));
    }

    @Test
    void getIntellectualProperty_ExistingId_Success() throws Exception {
        when(entitiesCrudService.getIntellectualProperty(1L)).thenReturn(validIpDto);
        mockMvc.perform(get("/api/v1/intellectual-properties/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(validIpDto.getId()))
                .andExpect(jsonPath("$.data.name").value(validIpDto.getName()))
                .andExpect(jsonPath("$.data.description").value(validIpDto.getDescription()));

        verify(entitiesCrudService).getIntellectualProperty(1L);
    }

    @Test
    void getAllIntellectualProperties_Success() throws Exception {
        List<IntellectualPropertyDto> intellectualProperties = Collections.singletonList(validIpDto);
        when(entitiesCrudService.getAllIntellectualProperties()).thenReturn(intellectualProperties);

        mockMvc.perform(get("/api/v1/intellectual-properties"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(entitiesCrudService).getAllIntellectualProperties();
    }

    @Test
    void updateIntellectualProperty_ValidInput_Success() throws Exception {
        when(entitiesCrudService.updateIntellectualProperty(any(IntellectualPropertyDto.class))).thenReturn(validIpDto);

        mockMvc.perform(put("/api/v1/intellectual-properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIpDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data.id").value(validIpDto.getId()))
                .andExpect(jsonPath("$.data.data.name").value(validIpDto.getName()))
                .andExpect(jsonPath("$.data.data.description").value(validIpDto.getDescription()));

        verify(entitiesCrudService).updateIntellectualProperty(any(IntellectualPropertyDto.class));
    }

    @Test
    void deleteIntellectualProperty_ExistingId_Success() throws Exception {
        doNothing().when(entitiesCrudService).deleteIntellectualProperty(1L);

        mockMvc.perform(delete("/api/v1/intellectual-properties/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(entitiesCrudService).deleteIntellectualProperty(1L);
    }
}
