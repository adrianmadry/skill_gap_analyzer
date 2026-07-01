package com.skillgap.dto.external;

import java.util.List;

import com.skillgap.entity.enums.JobRoleTag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Represents the data provided by the user to execute a skill gap analysis.")
public record SkillGapRequestDto(
    
    @NotEmpty(message = "User skills list cannot be empty. Please provide at least one skill.")
    @Size(max = 50, message = "Cannot analyze more than 50 skills at once.")
    @Schema(description = "List of skills currently possessed by the user.", 
            example = "[\"Java\", \"Git\", \"Hibernate\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    List<String> userSkills,

    @NotNull(message = "Target role cannot be empty. Please provide role.")
    @Schema(description = "The target professional role to analyze against market demands.", 
            example = "JAVA_DEVELOPER",
            requiredMode = Schema.RequiredMode.REQUIRED)
    JobRoleTag targetRole,

    //TODO: fallback dla miasto nie istnieje w bazie
    @Size(max = 100, message = "City name is too long. Maximum length is 100 characters.")
    @Schema(description = "Optional city filter to narrow down regional market requirements. If null, global data is used.", 
            example = "Warszawa",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String city

) {}
