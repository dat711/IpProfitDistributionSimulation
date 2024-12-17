package com.LegalEntitiesManagement.v1.Entities.dto;

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

    @NotNull
    @NotBlank(message = "Contract description must not be blank")
    @Size(min = 10, max = 200,
            message = "Description must be between 10 and 200 characters")
    private String description;

    @NotNull
    @NotEmpty(message = "The contract active date should be specified")
    private LocalDate contractActiveDate;

    @NotNull
    @Min(value = 0, message = "The contract priority must be greater than 0")
    private Integer contractPriority;

    @NotNull
    @Min(value = 0, message = "The executor id must be greater than 0")
    private Long executorId;
    public ContractDto(String description, LocalDate contractActiveDate, Integer contractPriority, Long executorId) {
        this.description = description;
        this.contractActiveDate = contractActiveDate;
        this.contractPriority = contractPriority;
        this.executorId = executorId;
    }
}
