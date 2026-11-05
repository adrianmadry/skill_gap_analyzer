package com.skillgap.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    
    @NotBlank(message = "User email cannot be blank")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    @Email
    @Schema(description = "User email", 
            example = "test@test.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    String email,
    
    @NotBlank(message = "User password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Schema(description = "User password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String password,

    @NotBlank(message = "Confirmation password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Schema(description = "Confirm user password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String confirmPassword

) {}
