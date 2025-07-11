package org.gustavoventieri.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.gustavoventieri.domain.enums.RequestStatus;

public record FriendshipDomain(
    UUID id,
    UserDomain user,
    UserDomain friend,
    UserDomain requestedBy,
    RequestStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
