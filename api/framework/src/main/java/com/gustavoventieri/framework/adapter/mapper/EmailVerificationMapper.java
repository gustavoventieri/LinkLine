package com.gustavoventieri.framework.adapter.mapper;


import java.time.ZoneId;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;

import com.gustavoventieri.framework.entity.EmailConfirmation;


public class EmailVerificationMapper {

    public static EmailConfirmationDomain toDomain(EmailConfirmation entity) {
        return new EmailConfirmationDomain(
            entity.getId(),
            entity.getEmail(),
            entity.getCode(),
            entity.getUsername(),
            entity.getPassword(),
            entity.isVerified(),
            entity.getExpiresAt() != null ? entity.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant() : null,
            entity.getCreatedAt()
        );
    }

    public static EmailConfirmation toEntity(EmailConfirmationDomain domain) {
        return new EmailConfirmation(
            domain.id(),
            domain.email(),
            domain.code(),
            domain.username(),
            domain.password(),
            domain.verified(),
            domain.expiresAt() != null ? domain.expiresAt().atZone(ZoneId.systemDefault()).toInstant() : null,
            domain.createdAt()
        );
    }
}

