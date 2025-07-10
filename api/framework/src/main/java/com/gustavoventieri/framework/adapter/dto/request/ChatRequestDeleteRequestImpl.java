package com.gustavoventieri.framework.adapter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChatRequestDeleteRequestImpl(  
    @NotBlank(message = "receiverId é obrigatório")
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "receiverId inválido")
    String receiverId) {
    
}
