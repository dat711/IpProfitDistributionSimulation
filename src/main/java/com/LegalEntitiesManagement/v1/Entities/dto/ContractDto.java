package com.LegalEntitiesManagement.v1.Entities.dto;

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
    private String description;
    private LocalDate contractActiveDate;
    private Integer contractPriority;
    private Long executorId;
    public ContractDto(String description, LocalDate contractActiveDate, Integer contractPriority, Long executorId) {
        this.description = description;
        this.contractActiveDate = contractActiveDate;
        this.contractPriority = contractPriority;
        this.executorId = executorId;
    }
}
