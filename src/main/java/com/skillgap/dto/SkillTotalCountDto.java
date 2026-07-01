package com.skillgap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the total occurrence count of a specific skill across analyzed job offers.")
public record SkillTotalCountDto(
    
    @Schema(description = "Skill identifier", example = "13")
    Long id,

    @Schema(description = "Skill name", example = "Hibernate")
    String skillName,

    @Schema(description = "Total number of job offers containing the skill", example = "22")
    Long totalCount

) {}
