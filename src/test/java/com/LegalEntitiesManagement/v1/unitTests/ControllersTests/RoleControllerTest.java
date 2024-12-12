package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;

import com.LegalEntitiesManagement.v1.Entities.controllers.RoleController;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import com.LegalEntitiesManagement.v1.V1Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import org.junit.jupiter.api.BeforeEach;

@WebMvcTest(RoleController.class)
@ContextConfiguration(classes = V1Application.class)
public class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleDto roleDto;

    private List<RoleDto> roleDtos;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto(1L, "Admin", "Administrator role", 1);
        roleDtos = Arrays.asList(
                new RoleDto(1L, "Admin", "Administrator role", 1),
                new RoleDto(2L, "User", "Regular user role", 2)
        );
    }

    @Test
    void createRole_ValidInput_ReturnsCreated() throws Exception {
        when(entitiesCrudService.addRole(any(RoleDto.class))).thenReturn(roleDto);
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/roles/1"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Admin"))
                .andExpect(jsonPath("$.data.description").value("Administrator role"))
                .andExpect(jsonPath("$.data.priority").value(1))
                .andExpect(jsonPath("$.message").value("Role saved successfully"));
    }
}
