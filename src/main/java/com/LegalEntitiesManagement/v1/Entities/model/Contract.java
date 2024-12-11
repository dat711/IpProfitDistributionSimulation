package com.LegalEntitiesManagement.v1.Entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity(name="profit_distribution_contract")
@Table(name ="profit_distribution_contract", schema = "public")
@DiscriminatorColumn(name = "contract_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("STANDARD")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SoftDelete
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intellectual_properties_id_seq")
    @SequenceGenerator(name = "intellectual_properties_id_seq", sequenceName = "intellectual_properties_id_seq",
            allocationSize = 1)
    private Long id;

    @Column(name="description")
    private String description;

    @Column(name="contract_active_date")
    private LocalDate contractActiveDate;

    @Column(name="contract_priority")
    private Integer contractPriority;

    @OneToMany(mappedBy="contract")
    private Set<ContractParticipant> contractParticipants;

    // Add direct reference to executor stakeholder
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_stakeholder_id")
    private StakeHolder executor;

    public Contract(String description, LocalDate contractActiveDate, Integer contractPriority, StakeHolder executor) {
        this.description = description;
        this.contractActiveDate = contractActiveDate;
        this.contractPriority = contractPriority;
        this.executor = executor;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", contractActiveDate=" + contractActiveDate +
                ", contractPriority=" + contractPriority +
                '}';
    }
}
