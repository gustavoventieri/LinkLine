package com.gustavoventieri.framework.adapter.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PrivateChatGetByIdRequest(
    @NotNull
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "chatId deve ser UUID válido")
    UUID chatId
) {}