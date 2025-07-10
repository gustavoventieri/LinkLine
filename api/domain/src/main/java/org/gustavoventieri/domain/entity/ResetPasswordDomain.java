package org.gustavoventieri.domain.entity;

import java.time.Instant;
import java.time.LocalDateTime;

public record ResetPasswordDomain(
    Long id,
    String email,
    String code,
    Instant expiresAt,
    LocalDateTime createdAt
) {}
