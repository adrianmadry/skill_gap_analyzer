package com.skillgap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Co-occurrence statistics of a skill relative to a given base skill")
public record SkillCoCountDto(
   
    @Schema(description = "Skill identifier", example = "13")
    Long id,
    
    @Schema(description = "Skill name", example = "Docker")
    String skillName,

    @Schema(description = "Number of job offers where this skill co-occurs with the base skill", example = "34")
    Long coOccurrenceCount
) {}
