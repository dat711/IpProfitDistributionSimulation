package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;
import com.LegalEntitiesManagement.v1.Entities.controllers.IpBasedContractController;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;
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

@WebMvcTest(IpBasedContractController.class)
public class IpBasedContractControllerTest extends BaseControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntitiesCrudService entitiesCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private IpBasedContractCompositionDto testContractComposition;
    private static final String BASE_URL = "/api/v1/ip-contracts";

    @BeforeEach
    void setUp() {
        // Create test IpBasedContractDto
        IpBasedContractDto contractDto = new IpBasedContractDto(
                "Test IP Contract",
                LocalDate.now(),
                1,
                1L,
                1L  // ipId
        );

        // Create test ParticipantDto
        ParticipantDto participantDto1 = new ParticipantDto(
                1L,
                0.6,
                true
        );

        ParticipantDto participantDto2 = new ParticipantDto(
                2L,
                0.4,
                false
        );

        Set<ParticipantDto> participants = new HashSet<>();
        participants.add(participantDto1);
        participants.add(participantDto2);

        // Create test IpBasedContractCompositionDto
        testContractComposition = new IpBasedContractCompositionDto(contractDto, participants);
    }

    @Test
    void whenCreateIpBasedContract_thenSuccess() throws Exception {
        // Given
        when(entitiesCrudService.saveIpBasedContract(any(IpBasedContractCompositionDto.class)))
                .thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(entitiesCrudService).saveIpBasedContract(any(IpBasedContractCompositionDto.class));
    }

    @Test
    void whenCreateBatchIpBasedContracts_thenSuccess() throws Exception {
        // Given
        List<IpBasedContractCompositionDto> contractsList = List.of(testContractComposition);
        when(entitiesCrudService.savedAllIpBasedContract(anyList()))
                .thenReturn(contractsList);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractsList)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(entitiesCrudService).savedAllIpBasedContract(anyList());
    }

    @Test
    void whenGetExistingIpBasedContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        when(entitiesCrudService.getIpBasedContract(contractId)).thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.Link").exists());

        verify(entitiesCrudService).getIpBasedContract(contractId);
    }

    @Test
    void whenGetNonExistingIpBasedContract_thenNotFound() throws Exception {
        // Given
        Long contractId = 999L;
        when(entitiesCrudService.getIpBasedContract(contractId))
                .thenThrow(new ContractNotFoundException(contractId));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(entitiesCrudService).getIpBasedContract(contractId);
    }

    @Test
    void whenGetAllIpBasedContracts_thenSuccess() throws Exception {
        // Given
        List<IpBasedContractCompositionDto> contracts = List.of(testContractComposition);
        when(entitiesCrudService.getAllIpBasedContracts()).thenReturn(contracts);

        // When & Then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(entitiesCrudService).getAllIpBasedContracts();
    }

    @Test
    void whenUpdateExistingIpBasedContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        testContractComposition.getContractDto().setId(contractId);
        when(entitiesCrudService.updateIpBasedContract(any(IpBasedContractCompositionDto.class)))
                .thenReturn(testContractComposition);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());

        verify(entitiesCrudService).updateIpBasedContract(any(IpBasedContractCompositionDto.class));
    }

    @Test
    void whenUpdateNonExistingIpBasedContract_thenNotFound() throws Exception {
        // Given
        Long contractId = 999L;
        testContractComposition.getContractDto().setId(contractId);
        doThrow(new ContractNotFoundException(contractId))
                .when(entitiesCrudService).updateIpBasedContract(any(IpBasedContractCompositionDto.class));

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContractComposition)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(entitiesCrudService).updateIpBasedContract(any(IpBasedContractCompositionDto.class));
    }

    @Test
    void whenDeleteExistingIpBasedContract_thenSuccess() throws Exception {
        // Given
        Long contractId = 1L;
        doNothing().when(entitiesCrudService).deleteIpBasedContract(contractId);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(entitiesCrudService).deleteIpBasedContract(contractId);
    }

    @Test
    void whenDeleteNonExistingIpBasedContract_thenNotFound() throws Exception {
        // Given
        Long contractId = 999L;
        doThrow(new ContractNotFoundException(contractId))
                .when(entitiesCrudService).deleteIpBasedContract(contractId);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", contractId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(entitiesCrudService).deleteIpBasedContract(contractId);
    }

    @Test
    void whenCreateIpBasedContractWithInvalidData_thenBadRequest() throws Exception {
        // Given
        IpBasedContractDto invalidContractDto = new IpBasedContractDto();  // Missing required fields
        IpBasedContractCompositionDto invalidComposition = new IpBasedContractCompositionDto(invalidContractDto, new HashSet<>());

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComposition)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(entitiesCrudService, never()).saveIpBasedContract(any(IpBasedContractCompositionDto.class));
    }

    @Test
    void whenCreateBatchIpBasedContractsWithInvalidData_thenBadRequest() throws Exception {
        // Given
        IpBasedContractDto invalidContractDto = new IpBasedContractDto();  // Missing required fields
        IpBasedContractCompositionDto invalidComposition = new IpBasedContractCompositionDto(invalidContractDto, new HashSet<>());
        List<IpBasedContractCompositionDto> invalidList = List.of(invalidComposition);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidList)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation errors in your request")); // Added explicit validation error check

        verify(entitiesCrudService, never()).savedAllIpBasedContract(anyList());
    }
}
