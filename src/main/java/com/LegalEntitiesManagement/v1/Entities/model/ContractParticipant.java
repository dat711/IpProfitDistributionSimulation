package com.LegalEntitiesManagement.v1.Entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ContractParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_participant_id_seq")
    @SequenceGenerator(name = "contract_participant_id_seq", sequenceName = "contract_participant_id_seq",
            allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name="contract_id")
    private Contract contract;

    @Column(name="participant_percentage")
    private Double percentage;

    @Column(name = "is_executor", nullable = false)
    private Boolean isExecutor = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stakeholder_id", nullable = false)
    private StakeHolder stakeholder;

    public ContractParticipant(Contract contract, Double percentage, Boolean isExecutor, StakeHolder stakeholder) {
        this.contract = contract;
        this.percentage = percentage;
        this.isExecutor = isExecutor;
        this.stakeholder = stakeholder;
    }

    @Override
    public String toString() {
        return "ContractParticipant{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", isExecutor=" + isExecutor +
                '}';
    }
}
