package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {
    private Long id;

    @NotBlank(message = "Contract description must not be blank", groups = SingleValidation.class)
    @Size(min = 10, max = 200,
            message = "Description must be between 10 and 200 characters", groups = SingleValidation.class)
    private String description;

    @NotNull(message = "The active date must be specified", groups = SingleValidation.class)
    @NotEmpty(message = "The contract active date should be specified", groups = SingleValidation.class)
    private LocalDate contractActiveDate;

    @NotNull(groups = SingleValidation.class)
    @Min(value = 0, message = "The contract priority must be greater than 0", groups = SingleValidation.class)
    private Integer contractPriority;

    @NotNull(groups = SingleValidation.class)
    @Min(value = 0, message = "The executor id must be greater than 0", groups = SingleValidation.class)
    private Long executorId;
    public ContractDto(String description, LocalDate contractActiveDate, Integer contractPriority, Long executorId) {
        this.description = description;
        this.contractActiveDate = contractActiveDate;
        this.contractPriority = contractPriority;
        this.executorId = executorId;
    }
}
