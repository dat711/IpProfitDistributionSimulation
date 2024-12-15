package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;


import com.LegalEntitiesManagement.v1.Entities.controllers.RoleController;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
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

@WebMvcTest(RoleController.class)
public class RoleControllerTest extends BaseControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleDto validRoleDto;
    private RoleDto invalidRoleDto;

    @BeforeEach
    void setUp() {
        // Set up valid role DTO
        validRoleDto = new RoleDto();
        validRoleDto.setId(1L);
        validRoleDto.setName("Admin");
        validRoleDto.setDescription("Administrator role");
        validRoleDto.setPriority(1);

        // Set up invalid role DTO
        invalidRoleDto = new RoleDto();
        invalidRoleDto.setId(2L);
        invalidRoleDto.setName("");  // Invalid: empty name
        invalidRoleDto.setDescription("");  // Invalid: empty description
        invalidRoleDto.setPriority(-1);  // Invalid: negative priority
    }

    @Test
    void createRole_ValidInput_Success() throws Exception {
        when(entitiesCrudService.addRole(any(RoleDto.class))).thenReturn(validRoleDto);

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRoleDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.data.id").value(validRoleDto.getId()))
                .andExpect(jsonPath("$.data.data.name").value(validRoleDto.getName()))
                .andExpect(jsonPath("$.data.data.description").value(validRoleDto.getDescription()))
                .andExpect(jsonPath("$.data.data.priority").value(validRoleDto.getPriority()));

        verify(entitiesCrudService).addRole(any(RoleDto.class));
    }

    @Test
    void createRole_InvalidInput_BadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).addRole(any(RoleDto.class));
    }

    @Test
    void getRole_ExistingId_Success() throws Exception {
        when(entitiesCrudService.getRole(1L)).thenReturn(validRoleDto);
        mockMvc.perform(get("/api/v1/roles/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(validRoleDto.getId()))
                .andExpect(jsonPath("$.data.name").value(validRoleDto.getName()));

        verify(entitiesCrudService).getRole(1L);
    }

    @Test
    void getAllRoles_Success() throws Exception {
        List<RoleDto> roles = Collections.singletonList(validRoleDto);
        when(entitiesCrudService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/roles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data.id").value(validRoleDto.getId()))
                .andExpect(jsonPath("$[0].data.name").value(validRoleDto.getName()));

        verify(entitiesCrudService).getAllRoles();
    }

    @Test
    void updateRole_ValidInput_Success() throws Exception {
        when(entitiesCrudService.updateRole(any(RoleDto.class))).thenReturn(validRoleDto);

        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRoleDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data.id").value(validRoleDto.getId()))
                .andExpect(jsonPath("$.data.data.name").value(validRoleDto.getName()));

        verify(entitiesCrudService).updateRole(any(RoleDto.class));
    }

    @Test
    void updateRole_InvalidInput_BadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(entitiesCrudService, never()).updateRole(any(RoleDto.class));
    }
}
