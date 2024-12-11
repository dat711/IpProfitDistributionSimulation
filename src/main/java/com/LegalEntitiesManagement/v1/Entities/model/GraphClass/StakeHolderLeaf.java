package com.LegalEntitiesManagement.v1.Entities.model.GraphClass;

import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("STAKEHOLDER")
public class StakeHolderLeaf extends MoneyNode{
    @OneToOne
    @JoinColumn(name = "stakeholder_id")
    private StakeHolder stakeHolder;

    public StakeHolderLeaf(StakeHolder stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StakeHolderLeaf that)) return false;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(stakeHolder.getId(), that.stakeHolder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                "MoneyNode-" + getId(),
                "StakeHolder-" + (stakeHolder != null ? stakeHolder.getId() : null)
        );
    }
}
