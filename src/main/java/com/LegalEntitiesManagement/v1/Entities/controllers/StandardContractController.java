package com.LegalEntitiesManagement.v1.Entities.controllers;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SpecialResponseBody;
import com.LegalEntitiesManagement.v1.Entities.dto.ContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/standard-contracts")
public class StandardContractController {
    private final EntitiesCrudService entitiesCrudService;

    public StandardContractController(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @PostMapping
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createContract(
            @Valid @Validated(SingleValidation.class)
            @RequestBody ContractCompositionDto contractDto,
            BindingResult bindingResult) {

        ContractCompositionDto savedContract = entitiesCrudService.saveContract(contractDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(
                        String.format("/api/v1/standard-contracts/%s",
                                savedContract.getContractDto().getId())))
                .body(SpecialResponseBody.getSuccessResponseContract(
                        savedContract,
                        "/api/v1/standard-contracts",
                        "Standard Contract created successfully"));
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getContract(@PathVariable long id) {
        ContractCompositionDto contract = entitiesCrudService.getContract(id);
        return ResponseEntity.ok(SpecialResponseBody.addLinkToContractComposition(
                contract,
                "/api/v1/standard-contracts/%s"));
    }

    @PutMapping("/{id}")
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> updateContract(
            @PathVariable long id,
            @Valid @Validated(SingleValidation.class)
            @RequestBody ContractCompositionDto contractDto,
            BindingResult bindingResult) {

        contractDto.getContractDto().setId(id);
        ContractCompositionDto updatedContract = entitiesCrudService.updateContract(contractDto);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SpecialResponseBody.getSuccessResponseContract(
                        updatedContract,
                        "/api/v1/standard-contracts",
                        "Standard Contract updated successfully"));
    }

    @DeleteMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> deleteContract(@PathVariable long id) {
        entitiesCrudService.deleteContract(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.deleteObject("Standard Contract", id));
    }

    @GetMapping
    public ResponseEntity<Object> getAllContracts() {
        List<ContractCompositionDto> contracts = entitiesCrudService.getAllContracts();
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.addLinksToListContractComposition(contracts, "/api/v1/standard-contracts/%s"));
    }
}
