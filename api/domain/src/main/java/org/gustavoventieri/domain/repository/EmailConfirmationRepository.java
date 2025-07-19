package org.gustavoventieri.domain.repository;

import java.time.Instant;
import java.util.Optional;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;

public interface EmailConfirmationRepository {

    EmailConfirmationDomain saveOrUpdate(String email, String username, String password, String code, boolean verified, Instant expiresAt);

    Optional<EmailConfirmationDomain> findByEmailAndCode(String email, String code);

    Optional<EmailConfirmationDomain> findByEmail(String email);

    void deleteByEmail(String email);
}