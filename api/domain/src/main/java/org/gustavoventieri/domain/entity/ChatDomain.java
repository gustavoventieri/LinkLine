package org.gustavoventieri.domain.entity;

import org.gustavoventieri.domain.enums.ChatType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ChatDomain(
    UUID id,
    ChatType type,
    String name,              
    String avatarUrl,         
    Set<UserDomain> participants,
    Set<MessageDomain> messages,
    LocalDateTime createdAt
) {}