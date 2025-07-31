package com.gustavoventieri.framework.adapter.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
    @NotBlank(message = "Email cannot be empty.")
    @Email
    String email, 

    @NotBlank(message = "Code cannot be empty.")
    @Size(min = 6, message = "Code cannot be less than 6 characters.")
    @Size(max = 6, message = "Code cannot exceed 6 characters.")
    String code,

    @NotBlank(message = "avatarUrl cannot be empty.")
    @Size(min = 8, message = "avatarUrl cannot be less than 8 characters.")
    String avatarUrl
    ) {
    
}
