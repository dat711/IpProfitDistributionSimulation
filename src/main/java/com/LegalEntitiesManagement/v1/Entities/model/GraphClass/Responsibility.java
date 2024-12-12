package com.LegalEntitiesManagement.v1.Entities.model.GraphClass;

import com.LegalEntitiesManagement.v1.Common.Proxies.Edge;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "money_edge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Responsibility implements Edge<MoneyNode> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "money_edge_seq")
    @SequenceGenerator(name = "money_edge_seq", sequenceName = "money_edge_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private MoneyNode target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private MoneyNode source;

    @Column(name = "percentage", precision = 5, nullable = false)
    private double percentage;

    @Override
    public MoneyNode getTarget() {
        return target;
    }

    @Override
    public MoneyNode getSource() {
        return source;
    }

    @Override
    public double getQuantity() {
        return percentage;
    }

    public Responsibility(MoneyNode target, MoneyNode source, double percentage) {
        this.target = target;
        this.source = source;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Responsibility{" +
                "id=" + id +
                ", percentage=" + percentage +
                '}';
    }
}
