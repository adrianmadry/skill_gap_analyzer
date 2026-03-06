package com.skillgap.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.skillgap.entity.enums.ExperienceLevel;
import com.skillgap.entity.enums.OfferSource;
import com.skillgap.entity.enums.WorkModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@ToString(exclude = "skills")
public class JobOffer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(unique = true)
    private Integer externalId;

    @NotNull
    private String title;

    private String companyName;

    private String city;

    private String country;

    private String roleTag;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    private WorkModel workModel;

    @Column(name="description", length = 1000)
    private String description;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String currency;

    private LocalDate publishedDate;

    @Enumerated(EnumType.STRING)
    private OfferSource offerSource;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_offer_skills",
        joinColumns = @JoinColumn(name="job_offer_id"),
        inverseJoinColumns = @JoinColumn(name="skill_id")

    )
    private Set<Skill> skills = new HashSet<>();

    public JobOffer(int externalId, String title, String companyName, String city, String country, String roleTag,
            ExperienceLevel experienceLevel, WorkModel workModel, String description, BigDecimal salaryMin,
            BigDecimal salaryMax, String currency, LocalDate publishedDate, OfferSource offerSource) {
        this.externalId = externalId;
        this.title = title;
        this.companyName = companyName;
        this.city = city;
        this.country = country;
        this.roleTag = roleTag;
        this.experienceLevel = experienceLevel;
        this.workModel = workModel;
        this.description = description;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.currency = currency;
        this.publishedDate = publishedDate;
        this.offerSource = offerSource;
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
        skill.getJobOffers().add(this);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
        skill.getJobOffers().remove(this);
    }



    
    
}
