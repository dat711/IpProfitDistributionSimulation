package com.LegalEntitiesManagement.v1.Entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    @NotNull(message = "The stakeholder id must be provided")
    @Min(1)
    private Long stakeholderId;

    @NotNull(message = "The percentage received by a party must always be provided")
    @DecimalMin(value = "0.01", message = "The percentage received must be at least 1%")
    @DecimalMax(value = "0.99", message = "The maximum percentage received must be 99%")
    private Double percentage;

    @NotNull(message = "The participant must be specified if being the contract executor")
    private Boolean isExecutor;
}
