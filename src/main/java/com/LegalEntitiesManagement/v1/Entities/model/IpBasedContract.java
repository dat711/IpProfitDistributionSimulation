package com.LegalEntitiesManagement.v1.Entities.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("IP_BASED")
public class IpBasedContract extends Contract {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ip_id")
    private IntellectualProperty intellectualProperty;

    public IpBasedContract(String description, LocalDate contractActiveDate,
                           Integer contract_priority, IntellectualProperty intellectualProperty, StakeHolder executor) {
        super(description, contractActiveDate, contract_priority, executor);
        this.intellectualProperty = intellectualProperty;
    }
}
