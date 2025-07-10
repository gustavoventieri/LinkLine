package com.gustavoventieri.framework.adapter.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PrivateChatCreateRequest(
    @NotBlank(message = "username é obrigatório")
    String username
) {}
