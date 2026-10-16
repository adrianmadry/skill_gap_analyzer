package com.skillgap.dto.external;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description="Represents the data provided by the user to execute offers import from provider")
public record TriggerImportRequestDto(
   
    @NotBlank(message = "Provider name cannot be blank")
    @Schema(description = "Provider name to import from",
            requiredMode = Schema.RequiredMode.REQUIRED)
    String providerName

) {

    
}
