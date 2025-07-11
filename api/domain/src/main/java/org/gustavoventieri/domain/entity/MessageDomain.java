package org.gustavoventieri.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDomain(
    UUID id,
    String content,
    LocalDateTime sentAt,
    UserDomain sender,
    ChatDomain chat
) {}
