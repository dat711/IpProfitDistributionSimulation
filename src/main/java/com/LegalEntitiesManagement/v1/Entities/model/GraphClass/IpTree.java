package com.LegalEntitiesManagement.v1.Entities.model.GraphClass;

import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ip_tree", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class IpTree {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ip_tree_seq")
    @SequenceGenerator(name = "ip_tree_seq", sequenceName = "ip_tree_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intellectual_property_id", nullable = false, unique = true)
    private IntellectualProperty intellectualProperty;

    @OneToOne
    @JoinColumn(name = "root_contract_node_id", nullable = false)
    private ContractNode rootContractNode;

    public IpTree(IntellectualProperty intellectualProperty, ContractNode rootContractNode) {
        this.intellectualProperty = intellectualProperty;
        this.rootContractNode = rootContractNode;
    }
}
