package com.skillgap.dto.external;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for adding skills to a user's profile")
public record AdminAddSkillsRequestDto(

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    @Schema(description = "Id of the user to whom the skills will be added", 
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId,
    
    @NotEmpty(message = "Skills list cannot be empty. Please provide at least one skill.")
    @Size(max = 30, message = "Cannot add more than 30 skills at once.")
    @Schema(description = "List of skill names to add to user profile", 
            example = "[\"Java\", \"Git\", \"Hibernate\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    List<@NotBlank(message = "Skill name cannot be blank")
         @Size(max = 100) 
         String
    > userSkills

) {}


