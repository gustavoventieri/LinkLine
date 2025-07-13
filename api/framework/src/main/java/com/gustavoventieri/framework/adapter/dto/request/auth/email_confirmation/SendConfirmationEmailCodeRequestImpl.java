package com.gustavoventieri.framework.adapter.dto.request.auth.email_confirmation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendConfirmationEmailCodeRequestImpl( 
    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 2, message = "Username cannot be less than 2 characters.")
    @Size(max = 100, message = "Username cannot be more than 100 characters.")
    String username,

    @NotBlank(message = "Email cannot be empty.")
    @Email
    String email, 
    
    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, message = "Password cannot be less than 8 characters.")
    @Size(max = 32, message = "Password cannot exceed 50 characters.")
    String password) {
} 