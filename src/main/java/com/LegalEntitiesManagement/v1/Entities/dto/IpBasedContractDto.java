package com.LegalEntitiesManagement.v1.Entities.dto;

import com.LegalEntitiesManagement.v1.Common.requestConstraints.annotations.Marker.SingleValidation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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


    @NotNull(message = "The intellectual property id must be defined",groups = SingleValidation.class)
    @Min(value = 1, message = "The Ip ID should be at least 1",groups = SingleValidation.class)
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
