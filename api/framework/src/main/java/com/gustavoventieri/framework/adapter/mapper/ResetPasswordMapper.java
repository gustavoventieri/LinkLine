package com.gustavoventieri.framework.adapter.mapper;

import org.gustavoventieri.domain.entity.ResetPasswordDomain;

import com.gustavoventieri.framework.entity.ResetPassword;

public class ResetPasswordMapper {

    public static ResetPasswordDomain toDomain(ResetPassword entity) {

        return new ResetPasswordDomain(
                entity.getId(),
                entity.getEmail(),
                entity.getCode(),
                entity.isVerified(),
                entity.getExpiresAt() != null
                        ? entity.getExpiresAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : null,
                entity.getCreatedAt());
    }

    public static ResetPassword toEntity(ResetPasswordDomain domain) {

        return new ResetPassword(
                domain.id(),
                domain.email(),
                domain.code(),
                domain.verified(),
                domain.expiresAt() != null ? domain.expiresAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : null,
                domain.createdAt());
    }
}
