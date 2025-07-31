package com.gustavoventieri.framework.adapter.dto.request.auth.email_confirmation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendConfirmationEmailCodeRequestDTO( 
    @NotBlank(message = "Email cannot be empty.")
    @Email
    String email
    
   ) {
} 