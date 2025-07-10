package org.gustavoventieri.domain.entity;

import java.time.Instant;
import java.time.LocalDateTime;

public record EmailConfirmationDomain(
    Long id,
    String email,
    String code,
    String username,
    String password,
    boolean verified,
    Instant expiresAt,
    LocalDateTime createdAt
) {}
