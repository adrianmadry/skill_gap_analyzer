package com.skillgap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents co-occurrence statistics for a skill relative to a specified base skill.")
public record CoOccurrenceResultDto(
    @Schema(description = "Co-occurence skill's name", example = "Docker")
    String skillName,

    @Schema(description = "Nnumber of job offers where both skills appear together.", example = "120")
    Long coCount,

    @Schema(description = "The association strength - Jaccard Index", example = "0.45")
    Double coScore,

    @Schema(description = "The percentage of job offers containing the base skill that also include this skill.", example = "60.0")
    Double percentageOfOffers
) {}
