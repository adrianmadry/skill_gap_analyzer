package com.skillgap.dto.response;

import java.util.List;

import com.skillgap.dto.SkillRecommendationDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the skill gap analysis results.")
public record SkillGapResponseDto(
    
    @Schema(description = "Overall percentage match between user skills and market requirements for requested role", example = "65.5")
    double percentageMatch,

    @Schema(description = "List of critical skills that the user is missing.", example = "[\"Spring Boot\", \"SQL\"]")
    List<String> missingMustHaveSkillsNames,

    @Schema(description = "List of supplementary skills that would improve the user's profile.", example = "[\"Docker\", \"Git\"]")
    List<String> missingNiceToHaveSkillsNames,

    @Schema(description = "Recommendations on which missing skills to learn first, based on your current skillset overlap.", name = "recSkills", example = "[\"Docker\", \"Git\"]")
    List<SkillRecommendationDto> recommendedSkills

) {

}
