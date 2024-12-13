package com.LegalEntitiesManagement.v1.Entities.controllers;

import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SuccessResponse;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.InsertRoleDto;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final EntitiesCrudService entitiesCrudService;

    public RoleController(EntitiesCrudService entitiesCrudService) {
        this.entitiesCrudService = entitiesCrudService;
    }

    @PostMapping
    @AspectErrorsHandler
    @CheckRequestBody
    public ResponseEntity<Object> createRole(@Valid @InsertRoleDto @RequestBody RoleDto roleDto, BindingResult bindingResult) {
        RoleDto savedRole = entitiesCrudService.addRole(roleDto);
        SuccessResponse<RoleDto> successResponse = SuccessResponse.successResponse(savedRole, "Role saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(String.format("/api/v1/roles/%s", savedRole.getId())))
                .body(successResponse);
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getRole(@PathVariable long id){
        System.out.println(id);
        RoleDto role = entitiesCrudService.getRole(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles(){
        List<RoleDto> roles = entitiesCrudService.getAllRoles();
        return ResponseEntity.status(HttpStatus.OK).headers(ResponseHeadersHelper.getBaseHeaders()).body(roles);
    }
}
