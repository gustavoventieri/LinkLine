package com.gustavoventieri.framework.adapter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchUserRequest(
    @NotBlank(message = "O campo searchTerm é obrigatório.")
    String searchTerm,

    @NotNull
    @Min(value = 1, message = "O limite deve ser pelo menos 1.")
    Integer limit
) {}