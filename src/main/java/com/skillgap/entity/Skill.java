package com.skillgap.entity;

import java.util.HashSet;
import java.util.Set;

import com.skillgap.entity.enums.SkillCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "jobOffers")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotNull
    @Column(name="name", unique = true)
    private String name;

    @Column(name="description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="category")
    private SkillCategory category;

    @ManyToMany(fetch = FetchType.LAZY,
                mappedBy = "skills"
    )
    private Set<JobOffer> jobOffers = new HashSet<>();


    public Skill(String name, String description, SkillCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

  
}
