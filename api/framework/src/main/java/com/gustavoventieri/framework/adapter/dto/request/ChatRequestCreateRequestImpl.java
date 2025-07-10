package com.gustavoventieri.framework.adapter.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestCreateRequestImpl(
    @NotBlank(message = "username é obrigatório")
    String username
) {
} 