package com.skillgap.integration.justjoinit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JustJoinOfferDto(
    String guid,
    String title,
    List<Skill> requiredSkills,
    String city,
    String workplaceType,
    String experienceLevel,

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    Instant publishedAt,

    List<EmploymentType> employmentTypes
 
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Skill(
        String name,
        int level
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EmploymentType(
        BigDecimal from,
        BigDecimal to,
        String currency,
        String type,
        String unit
    ) {}
}


