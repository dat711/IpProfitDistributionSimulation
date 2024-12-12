package com.LegalEntitiesManagement.v1.Entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;

    @NotBlank(message = "The name of the role should not be blank")
    private String name;

    @NotBlank(message = "The description of the role should not be blank")
    private String description;

    @NotBlank(message = "The priority should be provided ")
    private int priority;

    public RoleDto(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }
}
