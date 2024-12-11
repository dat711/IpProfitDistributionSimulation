package com.LegalEntitiesManagement.v1.Entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Table(name="stakeholder", schema = "public")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class StakeHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stakeholder_id_seq")
    @SequenceGenerator(name = "stakeholder_id_seq", sequenceName = "stakeholder_id_seq", allocationSize = 1)
    private Long id;

    @Column(name="name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public StakeHolder(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StakeHolder that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,"StakeHolder", name, role.getId());
    }

    @Override
    public String toString() {
        return "StakeHolder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
