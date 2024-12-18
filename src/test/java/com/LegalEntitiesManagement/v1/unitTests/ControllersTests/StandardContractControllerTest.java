package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;
import com.LegalEntitiesManagement.v1.Entities.controllers.StandardContractController;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractDto;
import com.LegalEntitiesManagement.v1.Entities.dto.ParticipantDto;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StandardContractController.class)
public class StandardContractControllerTest extends BaseControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContractCompositionDto testContractComposition;
    private static final String BASE_URL = "/api/v1/standard-contracts";

    @BeforeEach
    void setUp() {
        // Create test ContractDto
        ContractDto contractDto = new ContractDto(
                "Test Contract",
                LocalDate.now(),
                1,
                1L
        );

        // Create test ParticipantDto
        ParticipantDto participantDto = new ParticipantDto(
                1L,
                0.6,
                true
        );

        ParticipantDto participantDto2 = new ParticipantDto(
                1L,
                0.4,
                false
        );

        Set<ParticipantDto> participants = new HashSet<>();
        participants.add(participantDto);
        participants.add(participantDto2);

        // Create test ContractCompositionDto
        testContractComposition = new ContractCompositionDto(contractDto, participants);
    }

    @Test
    void whenCreateContract_thenSuccess() throws Exception {
        // Given
        when(entitiesCrudService.saveContract(any(ContractCompositionDto.class)))
                .thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(entitiesCrudService).saveContract(any(ContractCompositionDto.class));
    }

    @Test
    void whenGetExistingContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        when(entitiesCrudService.getContract(contractId)).thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.Link").exists());

        verify(entitiesCrudService).getContract(contractId);
    }

    @Test
    void whenGetNonExistingContract_thenNotFound() throws Exception {
        // Given
        Long contractId = 999L;
        when(entitiesCrudService.getContract(contractId))
                .thenThrow(new ContractNotFoundException(contractId));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(entitiesCrudService).getContract(contractId);
    }

    @Test
    void whenGetAllContracts_thenSuccess() throws Exception {
        // Given
        List<ContractCompositionDto> contracts = List.of(testContractComposition);
        when(entitiesCrudService.getAllContracts()).thenReturn(contracts);

        // When & Then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(entitiesCrudService).getAllContracts();
    }

    @Test
    void whenUpdateExistingContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        testContractComposition.getContractDto().setId(contractId);
        when(entitiesCrudService.updateContract(any(ContractCompositionDto.class)))
                .thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(entitiesCrudService).updateContract(any(ContractCompositionDto.class));
    }

    @Test
    void whenUpdateNonExistingContract_thenNotFound() throws Exception {
        // Given
        Long contractId = 999L;
        testContractComposition.getContractDto().setId(contractId);
        doThrow(new ContractNotFoundException(contractId))
                .when(entitiesCrudService).updateContract(any(ContractCompositionDto.class));

//        when(entitiesCrudService.updateContract(any(ContractCompositionDto.class)))
//                .thenThrow(new ContractNotFoundException(contractId));

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(entitiesCrudService).updateContract(any(ContractCompositionDto.class));
    }

    @Test
    void whenDeleteExistingContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        testContractComposition.getContractDto().setId(contractId);
        when(entitiesCrudService.getContract(contractId)).thenReturn(testContractComposition);
        doNothing().when(entitiesCrudService).deleteContract(anyLong());

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(entitiesCrudService).deleteContract(anyLong());
    }

    @Test
    void whenDeleteNonExistingContract_thenNotFound() throws Exception {
        // Given
        long contractId = 999L;
        doThrow(new ContractNotFoundException(contractId))
                .when(entitiesCrudService).deleteContract(anyLong());

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateContractWithInvalidData_thenBadRequest() throws Exception {
        // Given
        ContractDto invalidContractDto = new ContractDto();  // Missing required fields
        ContractCompositionDto invalidComposition = new ContractCompositionDto(invalidContractDto, new HashSet<>());

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComposition)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).saveContract(any(ContractCompositionDto.class));
    }
}
