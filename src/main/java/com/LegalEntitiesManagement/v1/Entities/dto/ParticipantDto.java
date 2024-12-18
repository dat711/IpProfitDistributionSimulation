package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;
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
    @NotNull(message = "The stakeholder id must be provided", groups = SingleValidation.class)
    @Min(value = 1, message = "The stakeholder id must be at least 1", groups = SingleValidation.class)
    private Long stakeholderId;

    @NotNull(message = "The percentage received by a party must always be provided", groups = SingleValidation.class)
    @DecimalMin(value = "0.01", message = "The percentage received must be at least 1%", groups = SingleValidation.class)
    @DecimalMax(value = "0.99", message = "The maximum percentage received must be 99%", groups = SingleValidation.class)
    private Double percentage;

    @NotNull(message = "The participant must be specified if being the contract executor", groups = SingleValidation.class)
    private Boolean isExecutor;
}
