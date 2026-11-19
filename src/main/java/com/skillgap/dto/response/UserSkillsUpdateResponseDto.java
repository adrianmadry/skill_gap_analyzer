package com.skillgap.dto.response;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing the result of adding skills to a user's profile")
public record UserSkillsUpdateResponseDto(
    
    @Schema(description = "User Id", example = "1")
    Long userId,

    @Schema(description = "Set of skill names that were successfully validated and added to the user's profile", 
            example = "[\"Java\", \"Spring Boot\", \"Docker\"]")
    Set<String> skillsAddedToUserProfile,

    @Schema(description = "Set of skill names that were rejected because they do not exist in the database dictionary", 
            example = "[\"UnknownTechnology1\", \"InvalidSkillName\"]")
    Set<String> skillsSkipped

) {}

