package com.skillgap.dto.external;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSkillRequestDto(

    @NotBlank(message = "Skill name cannot be empty")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    @Schema(description = "Official skill name", example = "Spring Boot")
    String skillName,

    @Schema(description = "List of aliases for the skill", example = "[\"springboot\", \"spring-boot\"]")
    List<@NotBlank String> aliases

) {}
