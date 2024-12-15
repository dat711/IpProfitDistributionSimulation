package com.LegalEntitiesManagement.v1.Entities.controllers;

import com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import com.LegalEntitiesManagement.v1.Common.aspects.helpers.SpecialResponseBody;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.InsertRoleDto;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.ValidUpdate;
import com.LegalEntitiesManagement.v1.Entities.dto.RoleDto;
import com.LegalEntitiesManagement.v1.Entities.services.EntitiesCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler;

import java.util.HashSet;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(ResponseHeadersHelper.getSuccessPostHeaders(String.format("/api/v1/roles/%s", savedRole.getId())))
                .body(SpecialResponseBody.getSuccessResponses(RoleDto.class, savedRole, "/api/v1/roles",
                        "Role created successfully"));
    }

    @GetMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> getRole(@PathVariable long id){
        RoleDto role = entitiesCrudService.getRole(id);
        return ResponseEntity.status(HttpStatus.OK).headers(ResponseHeadersHelper.getBaseHeaders()).body(
                SpecialResponseBody.addLink(RoleDto.class , role, "/api/v1/roles")
        );
    }

    @GetMapping
    public ResponseEntity<Object> getAllRoles(){
        List<RoleDto> roles = entitiesCrudService.getAllRoles();
        return ResponseEntity.status(HttpStatus.OK).headers(ResponseHeadersHelper.getBaseHeaders()).body(
                SpecialResponseBody.addLinks(RoleDto.class , new HashSet<>(roles), "/api/v1/roles")
        );
    }

    @PutMapping("/{id}")
    @AspectErrorsHandler
    @CheckRequestBody
    @ValidUpdate(entity = "role")
    public ResponseEntity<Object> updateRole(@PathVariable long id,
                                              @Valid @RequestBody RoleDto roleDto, BindingResult bindingResult){
        roleDto.setId(id);
        RoleDto updatedRole = this.entitiesCrudService.updateRole(roleDto);
        return ResponseEntity.status(HttpStatus.OK).headers(ResponseHeadersHelper.getSuccessGetPutHeaders())
                .body(SpecialResponseBody.getSuccessResponses(RoleDto.class, updatedRole, "/api/v1/roles",
                        "Role updated successfully"));
    }

    @DeleteMapping("/{id}")
    @AspectErrorsHandler
    public ResponseEntity<Object> deleteRole(@PathVariable long id){
        entitiesCrudService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(ResponseHeadersHelper.getBaseHeaders())
                .body(SpecialResponseBody.deleteObject("role", id));
    }
}
