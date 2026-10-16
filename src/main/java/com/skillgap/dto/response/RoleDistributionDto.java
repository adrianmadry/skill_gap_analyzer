package com.skillgap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents market share statistics for a specific job role across imported job offers.")
public record RoleDistributionDto(
    @Schema(description = "Name of the job role", example = "Java Developer")
    String roleName,

    @Schema(description = "Total number of job offers associated with this role", example = "350")
    long count,

    @Schema(description = "Percentage share of this role relative to all analyzed job offers", example = "28.5")
    double percentage
) {}
