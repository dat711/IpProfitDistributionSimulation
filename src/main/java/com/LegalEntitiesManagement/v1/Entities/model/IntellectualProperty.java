package com.LegalEntitiesManagement.v1.Entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Table(name ="intellectual_properties", schema = "public")
@Entity(name="intellectual_properties")
@Getter
@Setter
@NoArgsConstructor
public class IntellectualProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intellectual_properties_id_seq")
    @SequenceGenerator(name = "intellectual_properties_id_seq", sequenceName = "intellectual_properties_id_seq",
            allocationSize = 1)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    public IntellectualProperty(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
