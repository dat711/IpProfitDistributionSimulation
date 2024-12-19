package com.LegalEntitiesManagement.v1.Entities.controllers;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SpecialResponseBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SuccessResponse;
import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.dto.TreeRepresentation.TreeInfo;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/intellectual-properties")
public class IntellectualPropertyController {
    private final EntitiesCrudService entitiesCrudService;

    public IntellectualPropertyController(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @PostMapping
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createIntellectualProperty(@Valid @RequestBody IntellectualPropertyDto intellectualPropertyDto,
                                                             BindingResult bindingResult) {
        IntellectualPropertyDto savedIp = entitiesCrudService.addIntellectualProperty(intellectualPropertyDto);
        SuccessResponse<IntellectualPropertyDto> successResponse = SuccessResponse.successResponse(
                savedIp,
                "Intellectual Property created successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(
                        String.format("/api/v1/intellectual-properties/%s", savedIp.getId())
                ))
                .body(successResponse);
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getIntellectualProperty(@PathVariable long id) {
        IntellectualPropertyDto intellectualProperty = entitiesCrudService.getIntellectualProperty(id);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.addLink(IntellectualPropertyDto.class, intellectualProperty,
                        "/api/v1/intellectual-properties/%s"));
    }

    @GetMapping
    public ResponseEntity<Object> getAllIntellectualProperties() {
        List<IntellectualPropertyDto> intellectualProperties = entitiesCrudService.getAllIntellectualProperties();
        return ResponseEntity.status(HttpStatus.OK)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.addLinks(IntellectualPropertyDto.class,
                        new HashSet<>(intellectualProperties), "/api/v1/intellectual-properties/%s"));
    }

    @PutMapping("/{id}")
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> updateIntellectualProperty(@PathVariable long id,
                                                             @Valid @RequestBody IntellectualPropertyDto intellectualPropertyDto,
                                                             BindingResult bindingResult) {
        intellectualPropertyDto.setId(id);
        IntellectualPropertyDto updatedIp = entitiesCrudService.updateIntellectualProperty(intellectualPropertyDto);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SpecialResponseBody.getSuccessResponses(IntellectualPropertyDto.class, updatedIp,
                        "/api/v1/intellectual-properties", "Intellectual Property updated successfully"));
    }

    @DeleteMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> deleteIntellectualProperty(@PathVariable long id) {
        entitiesCrudService.deleteIntellectualProperty(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.deleteObject("Intellectual Property", id));
    }

    @GetMapping("/details/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getTreeRepresentation(@PathVariable long id){
        TreeInfo info = this.entitiesCrudService.getTreeRepresentation(id);
        return ResponseEntity.ok()
                .headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SuccessResponse.successResponse(
                        info,
                        "IP distribution tree retrieved successfully"
                ));
    }
}
