package com.LegalEntitiesManagement.v1.Entities.controllers;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SpecialResponseBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SuccessResponse;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ExistsConstraint;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidUpdate;
import com.LegalEntitiesManagement.v1.Entities.dto.StakeHolderDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stakeholders")
public class StakeHolderController {
    private final EntitiesCrudService entitiesCrudService;

    public StakeHolderController(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @PostMapping
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createStakeHolder(@Valid @RequestBody StakeHolderDto stakeHolderDto, BindingResult bindingResult) {
        StakeHolderDto savedStakeHolder = entitiesCrudService.addStakeHolder(stakeHolderDto);
        SuccessResponse<StakeHolderDto> successResponse = SuccessResponse.successResponse(
                savedStakeHolder,
                "StakeHolder saved successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(
                        String.format("/api/v1/stakeholders/%s", savedStakeHolder.getId())
                ))
                .body(successResponse);
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getStakeHolder(@PathVariable long id) {
        StakeHolderDto stakeHolder = entitiesCrudService.getStakeHolder(id);
        return ResponseEntity.ok(SpecialResponseBody.addLink(StakeHolderDto.class, stakeHolder,
                "/api/v1/stakeholders/%s"));
    }

    @GetMapping
    public ResponseEntity<Object> getAllStakeHolders() {
        List<StakeHolderDto> stakeHolders = entitiesCrudService.getAllStakeHolders();
        return ResponseEntity.status(HttpStatus.OK)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.addLinks(StakeHolderDto.class,
                        new HashSet<>(stakeHolders), "/api/v1/stakeholders/%s"));
    }

    @PutMapping("/{id}")
    @AspectErrorsHandler
    @CheckRequestBody
    @ValidUpdate(entity = "stakeholder")
    public ResponseEntity<Object> updateStakeHolder(
            @ExistsConstraint(entity = "stakeholder") @PathVariable long id,
            @Valid @RequestBody StakeHolderDto stakeHolderDto,
            BindingResult bindingResult) {
        stakeHolderDto.setId(id);
        StakeHolderDto updatedStakeHolder = entitiesCrudService.updateStakeHolder(stakeHolderDto);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SpecialResponseBody.getSuccessResponses(StakeHolderDto.class, updatedStakeHolder,
                        "/api/v1/stakeholders","StakeHolder updated successfully"));
    }

    @DeleteMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> deleteStakeHolder(@PathVariable long id){
        this.entitiesCrudService.deleteStakeHolder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(SpecialResponseBody.deleteObject("StakeHolder", id));
    }
}