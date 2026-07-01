package com.skillgap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A skill recommended to learn based on co-occurrence with the user's current skillset.")
public record SkillRecommendationDto (
    
    @Schema(description = "Skill identifier", example = "13")
    Long id,
    
    @Schema(description = "Skill name", example = "Docker")
    String skillName,

    @Schema(description = "Recommendation score — higher means more relevant", example = "320.0")
    double recScore,
    
    @Schema(description = "Market category indicating skill importance for the requested role", example = "MUST_HAVE")
    String category

) {}
