package com.LegalEntitiesManagement.v1.Entities.controllers;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SpecialResponseBody;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidIpBasedContractBatch;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.BatchValidation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ip-contracts")
public class IpBasedContractController {
    private final EntitiesCrudService entitiesCrudService;

    public IpBasedContractController(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @PostMapping
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createIpBasedContract(
            @Valid @Validated(SingleValidation.class) @ValidIpBasedContractBatch.IpBasedContractInsertValidator
            @RequestBody IpBasedContractCompositionDto contractDto,
            BindingResult bindingResult) {

        IpBasedContractCompositionDto savedContract = entitiesCrudService.saveIpBasedContract(contractDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(
                        String.format("/api/v1/ip-contracts/%s",
                                savedContract.getContractDto().getId())))
                .body(SpecialResponseBody.getSuccessResponseIpBasedContract(
                        savedContract,
                        "/api/v1/ip-contracts",
                        "IP-Based Contract created successfully"));
    }

    @PostMapping("/batch")
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createBatchIpBasedContracts(
            @Valid @Validated(BatchValidation.class) @ValidIpBasedContractBatch
            @RequestBody List<IpBasedContractCompositionDto> contracts,
            BindingResult bindingResult) {

        List<IpBasedContractCompositionDto> savedContracts = entitiesCrudService.savedAllIpBasedContract(contracts);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders("/api/v1/ip-contracts/batch"))
                .body(SpecialResponseBody.getSuccessResponseListIpBasedContract(
                        savedContracts,
                        "/api/v1/ip-contracts",
                        "IP-Based Contracts created successfully"));
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getIpBasedContract(@PathVariable long id) {
        IpBasedContractCompositionDto contract = entitiesCrudService.getIpBasedContract(id);
        return ResponseEntity.ok(SpecialResponseBody.addLinkToIpBasedContractComposition(
                contract,
                "/api/v1/ip-contracts/%s"));
    }

    @PutMapping("/{id}")
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> updateIpBasedContract(
            @PathVariable long id,
            @Valid @Validated(SingleValidation.class)
            @RequestBody IpBasedContractCompositionDto contractDto,
            BindingResult bindingResult) {

        contractDto.getContractDto().setId(id);
        IpBasedContractCompositionDto updatedContract = entitiesCrudService.updateIpBasedContract(contractDto);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SpecialResponseBody.getSuccessResponseIpBasedContract(
                        updatedContract,
                        "/api/v1/ip-contracts",
                        "IP-Based Contract updated successfully"));
    }

    @DeleteMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> deleteIpBasedContract(@PathVariable long id) {
        entitiesCrudService.deleteIpBasedContract(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.deleteObject("IP-Based Contract", id));
    }

    @GetMapping
    public ResponseEntity<Object> getAllIpBasedContracts() {
        List<IpBasedContractCompositionDto> contracts = entitiesCrudService.getAllIpBasedContracts();
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.addLinksToListIpBasedContractComposition(
                        contracts,
                        "/api/v1/ip-contracts/%s"));
    }
}
