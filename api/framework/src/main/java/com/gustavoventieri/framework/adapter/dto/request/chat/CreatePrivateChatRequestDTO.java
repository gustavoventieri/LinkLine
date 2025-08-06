package com.gustavoventieri.framework.adapter.dto.request.chat;

import jakarta.validation.constraints.NotBlank;

public record CreatePrivateChatRequestDTO(
        @NotBlank(message = "O nome de usuário do alvo não pode ser vazio.") String targetUsername) {

}