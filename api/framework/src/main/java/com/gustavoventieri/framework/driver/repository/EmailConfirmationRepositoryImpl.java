package com.gustavoventieri.framework.driver.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;
import org.gustavoventieri.domain.exception.BadRequest;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.repository.EmailConfirmationRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.EmailVerificationMapper;
import com.gustavoventieri.framework.driver.repository.client.EmailConfirmationOrm;
import com.gustavoventieri.framework.entity.EmailConfirmation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do repositório para operações relacionadas à verificação de
 * e-mail.
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class EmailConfirmationRepositoryImpl implements EmailConfirmationRepository {

    private final EmailConfirmationOrm emailVerificationOrm;

    /**
     * Salva ou atualiza o registro de verificação de e-mail.
     *
     * @param email     e-mail do usuário
     * @param username  nome de usuário
     * @param password  senha criptografada
     * @param code      código de verificação
     * @param verified  flag indicando se o e-mail já foi verificado
     * @param expiresAt data e hora de expiração do código
     * @return domínio EmailVerification salvo/atualizado
     * @throws BadRequest          em caso de violação de integridade dos dados
     * @throws InternalServerError em caso de erro inesperado
     */
    @Override
    public void saveOrUpdate(final String email, final String username, final String password,
            final String code, final boolean verified,
            final Instant expiresAt) {
        try {
            final Optional<EmailConfirmation> existing = emailVerificationOrm.findByEmail(email);

            final EmailConfirmation entity = existing
                    .map(ev -> {
                        ev.setUsername(username);
                        ev.setPassword(password);
                        ev.setCode(code);
                        ev.setVerified(verified);
                        ev.setExpiresAt(expiresAt);
                        ev.setCreatedAt(LocalDateTime.now());
                        return ev;
                    })
                    .orElse(new EmailConfirmation(null, email, code, username, password, verified, expiresAt,
                            LocalDateTime.now()));

            emailVerificationOrm.save(entity);
            log.info("EmailVerification saved/updated for email={}", email);

        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation while saving email verification for email={}: {}", email,
                    e.getMessage());
            throw new BadRequest("Data integrity violation while saving email verification", e);
        } catch (Exception e) {
            log.error("Internal error while saving/updating email verification for email={}", email, e);
            throw new InternalServerError("Internal error occurred while saving or updating email verification", e);
        }
    }

    /**
     * Busca EmailVerification por e-mail e código.
     *
     * @param email e-mail do usuário
     * @param code  código de verificação
     * @return Optional com EmailVerificationDomain caso encontrado
     * @throws InternalServerError em caso de erro inesperado
     */
    @Override
    public Optional<EmailConfirmationDomain> findByEmailAndCode(final String email, final String code) {
        try {
            return emailVerificationOrm
                    .findByEmailAndCode(email, code)
                    .map(EmailVerificationMapper::toDomain);
        } catch (Exception e) {
            log.error("Internal error while finding email verification by email={} and code={}", email, code, e);
            throw new InternalServerError("Internal error occurred while finding email verification by email and code",
                    e);
        }
    }

    /**
     * Busca EmailVerification por e-mail.
     *
     * @param email e-mail do usuário
     * @return Optional com EmailVerificationDomain caso encontrado
     * @throws InternalServerError em caso de erro inesperado
     */
    @Override
    public Optional<EmailConfirmationDomain> findByEmail(final String email) {
        try {
            return emailVerificationOrm
                    .findByEmail(email)
                    .map(EmailVerificationMapper::toDomain);
        } catch (Exception e) {
            log.error("Internal error while finding email verification by email={}", email, e);
            throw new InternalServerError("Internal error occurred while finding email verification by email", e);
        }
    }

    /**
     * Remove registro de verificação de e-mail pelo e-mail.
     *
     * @param email e-mail do usuário
     * @throws InternalServerError em caso de erro inesperado
     */
    @Override
    public void deleteByEmail(final String email) {
        try {
            emailVerificationOrm.deleteByEmail(email);
            log.info("EmailVerification deleted for email={}", email);
        } catch (Exception e) {
            log.error("Internal error while deleting email verification by email={}", email, e);
            throw new InternalServerError("Internal error occurred while deleting email verification by email", e);
        }
    }
}
