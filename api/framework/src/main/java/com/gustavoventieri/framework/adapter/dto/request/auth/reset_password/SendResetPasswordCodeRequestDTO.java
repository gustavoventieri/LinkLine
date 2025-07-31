package com.gustavoventieri.framework.adapter.dto.request.auth.reset_password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendResetPasswordCodeRequestDTO(
    @NotBlank(message = "Email cannot be empty.")
    @Email
    String email
) {
    
}
