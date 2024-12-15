package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;

import com.LegalEntitiesManagement.v1.Entities.controllers.StakeHolderController;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
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

@WebMvcTest(StakeHolderController.class)
public class StakeHolderControllerTest extends BaseControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private StakeHolderDto validStakeHolderDto;
    private StakeHolderDto invalidStakeHolderDto;

    @BeforeEach
    void setUp() {
        // Set up valid stakeholder DTO
        validStakeHolderDto = new StakeHolderDto();
        validStakeHolderDto.setId(1L);
        validStakeHolderDto.setName("John Doe");
        validStakeHolderDto.setRoleId(1L);

        // Set up invalid stakeholder DTO
        invalidStakeHolderDto = new StakeHolderDto();
        invalidStakeHolderDto.setId(2L);
        invalidStakeHolderDto.setName("");  // Invalid: empty name
        invalidStakeHolderDto.setRoleId(null);  // Invalid: null roleId
    }

    @Test
    void createStakeHolder_ValidInput_Success() throws Exception {
        when(entitiesCrudService.addStakeHolder(any(StakeHolderDto.class))).thenReturn(validStakeHolderDto);

        mockMvc.perform(post("/api/v1/stakeholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStakeHolderDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(validStakeHolderDto.getId()))
                .andExpect(jsonPath("$.data.name").value(validStakeHolderDto.getName()))
                .andExpect(jsonPath("$.data.roleId").value(validStakeHolderDto.getRoleId()));

        verify(entitiesCrudService).addStakeHolder(any(StakeHolderDto.class));
    }

    @Test
    void createStakeHolder_InvalidInput_BadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/stakeholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStakeHolderDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).addStakeHolder(any(StakeHolderDto.class));
    }

    @Test
    void getStakeHolder_ExistingId_Success() throws Exception {
        when(entitiesCrudService.getStakeHolder(1L)).thenReturn(validStakeHolderDto);

        mockMvc.perform(get("/api/v1/stakeholders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(validStakeHolderDto.getId()))
                .andExpect(jsonPath("$.data.name").value(validStakeHolderDto.getName()))
                .andExpect(jsonPath("$.data.roleId").value(validStakeHolderDto.getRoleId()));

        verify(entitiesCrudService).getStakeHolder(1L);
    }

    @Test
    void getAllStakeHolders_Success() throws Exception {
        List<StakeHolderDto> stakeholders = Collections.singletonList(validStakeHolderDto);
        when(entitiesCrudService.getAllStakeHolders()).thenReturn(stakeholders);

        mockMvc.perform(get("/api/v1/stakeholders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data.id").value(validStakeHolderDto.getId()))
                .andExpect(jsonPath("$[0].data.name").value(validStakeHolderDto.getName()))
                .andExpect(jsonPath("$[0].data.roleId").value(validStakeHolderDto.getRoleId()));

        verify(entitiesCrudService).getAllStakeHolders();
    }

    @Test
    void updateStakeHolder_ValidInput_Success() throws Exception {
        when(entitiesCrudService.updateStakeHolder(any(StakeHolderDto.class))).thenReturn(validStakeHolderDto);

        mockMvc.perform(put("/api/v1/stakeholders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStakeHolderDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data.id").value(validStakeHolderDto.getId()))
                .andExpect(jsonPath("$.data.data.name").value(validStakeHolderDto.getName()))
                .andExpect(jsonPath("$.data.data.roleId").value(validStakeHolderDto.getRoleId()));

        verify(entitiesCrudService).updateStakeHolder(any(StakeHolderDto.class));
    }

    @Test
    void updateStakeHolder_InvalidInput_BadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/stakeholders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStakeHolderDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).updateStakeHolder(any(StakeHolderDto.class));
    }

    @Test
    void deleteStakeHolder_ExistingId_Success() throws Exception {
        doNothing().when(entitiesCrudService).deleteStakeHolder(1L);

        mockMvc.perform(delete("/api/v1/stakeholders/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(entitiesCrudService).deleteStakeHolder(1L);
    }
}
