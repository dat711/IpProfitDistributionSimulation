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
public class IpBasedContractDto extends ContractDto {
    private Long ipId;
    public IpBasedContractDto(Long id, String description, LocalDate contractActiveDate, Integer contractPriority, Long executorId, Long ipId) {
        super(id, description, contractActiveDate, contractPriority, executorId);
        this.ipId = ipId;
    }

    public IpBasedContractDto(String description, LocalDate contractActiveDate, Integer contractPriority, Long executorId, Long ipId) {
        super(description, contractActiveDate, contractPriority, executorId);
        this.ipId = ipId;
    }
}
