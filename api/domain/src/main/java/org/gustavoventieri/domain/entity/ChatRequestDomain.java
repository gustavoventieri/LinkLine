package org.gustavoventieri.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.gustavoventieri.domain.enums.RequestStatus;

public record ChatRequestDomain(
    UUID id,
    UserDomain sender,
    UserDomain receiver,
    RequestStatus status,
    LocalDateTime createdAt
) {}
