package com.LegalEntitiesManagement.v1.Entities.model.GraphClass;

import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("CONTRACT")
public class ContractNode extends MoneyNode{
    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    public ContractNode(Contract contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        return "ContractNode{" +
                "id=" + this.getId() +
                " contract=" + contract +
                '}';
    }
}
