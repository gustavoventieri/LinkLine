package org.gustavoventieri.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrivateChatResponseDTO(
        UUID chatId,
        LocalDateTime createdAt,
        ParticipantDTO participant) {
}