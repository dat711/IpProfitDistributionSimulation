package com.LegalEntitiesManagement.v1.Entities.model.GraphClass;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.LegalEntitiesManagement.v1.Common.proxies.TransactionNode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "money_node", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "node_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyNode implements TransactionNode<Responsibility> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "money_node_seq")
    @SequenceGenerator(name = "money_node_seq", sequenceName = "money_node_seq", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Responsibility> downStreamEdges = new HashSet<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Responsibility> upStreamEdges = new HashSet<>();

    @Override
    public Set<Responsibility> getUpStreamEdges() {
        return upStreamEdges;
    }

    @Override
    public Set<Responsibility> getDownStreamEdges() {
        return downStreamEdges;
    }

    @Override
    public String toString() {
        return "MoneyNode{" +
                "id=" + id +
                '}';
    }
}
