package com.skillgap.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a popular technology stack combination for a specific job role with market statistics")
public record SkillStackDto(

    @Schema(description = "List of skill names forming the stack", example = "[\"Java\", \"Spring Boot\", \"Docker\"]")
    List<String> skills,

    @Schema(description = "Number of job offers containing this exact stack", example = "145")
    int occurrenceCount,

    @Schema(description = "Percentage of offers for this role containing the stack", example = "32.5")
    double popularityPercentage
) {}
