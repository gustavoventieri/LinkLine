package org.gustavoventieri.domain.repository;

import java.time.Instant;
import java.util.Optional;

import org.gustavoventieri.domain.entity.ResetPasswordDomain;

public interface ResetPasswordRepository {

    void saveOrUpdate(String email, String code, Instant expiresAt);

    Optional<ResetPasswordDomain> findByEmailAndCode(String email, String code);

    Optional<ResetPasswordDomain> findByEmail(String email);

    void deleteByEmail(String email);
}