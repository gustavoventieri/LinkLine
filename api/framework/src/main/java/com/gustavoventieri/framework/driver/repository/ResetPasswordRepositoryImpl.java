package com.gustavoventieri.framework.driver.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.gustavoventieri.domain.entity.ResetPasswordDomain;
import org.gustavoventieri.domain.exception.BadRequest;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.repository.ResetPasswordRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.ResetPasswordMapper;
import com.gustavoventieri.framework.driver.repository.client.ResetPasswordOrm;
import com.gustavoventieri.framework.entity.ResetPassword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the repository for operations related to Reset Password.
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class ResetPasswordRepositoryImpl implements ResetPasswordRepository {

    private final ResetPasswordOrm resetPasswordOrm;

    /**
     * Saves or updates the reset password record for the given email.
     *
     * @param email     the user's email
     * @param code      the reset password code
     * @param expiresAt the expiration date and time of the code
     * @throws BadRequest          in case of data integrity violation
     * @throws InternalServerError in case of unexpected error
     */
    @Override
    public void saveOrUpdate(final String email, final String code, final Instant expiresAt) {
        try {
            final Optional<ResetPassword> existing = resetPasswordOrm.findByEmail(email);

            final ResetPassword entity = existing
                    .map(rp -> {
                        rp.setCode(code);
                        rp.setExpiresAt(expiresAt);
                        rp.setCreatedAt(LocalDateTime.now());
                        return rp;
                    })
                    .orElse(new ResetPassword(null, email, code, expiresAt, LocalDateTime.now()));

            resetPasswordOrm.save(entity);
            log.info("ResetPassword saved/updated for email={}", email);
        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation while saving reset password for email={}: {}", email, e.getMessage());
            throw new BadRequest("Data integrity violation while saving reset password", e);
        } catch (Exception e) {
            log.error("Internal error while saving/updating reset password for email={}", email, e);
            throw new InternalServerError("Internal error occurred while saving or updating reset password", e);
        }
    }

    /**
     * Finds ResetPassword by email and code.
     *
     * @param email the user's email
     * @param code  the reset password code
     * @return Optional with ResetPasswordDomain if found
     * @throws InternalServerError in case of unexpected error
     */
    @Override
    public Optional<ResetPasswordDomain> findByEmailAndCode(final String email, final String code) {
        try {
            return resetPasswordOrm
                    .findByEmailAndCode(email, code)
                    .map(ResetPasswordMapper::toDomain);
        } catch (Exception e) {
            log.error("Internal error while finding reset password by email={} and code={}", email, code, e);
            throw new InternalServerError("Internal error occurred while finding reset password by email and code", e);
        }
    }

    /**
     * Finds ResetPassword by email.
     *
     * @param email the user's email
     * @return Optional with ResetPasswordDomain if found
     * @throws InternalServerError in case of unexpected error
     */
    @Override
    public Optional<ResetPasswordDomain> findByEmail(final String email) {
        try {
            return resetPasswordOrm
                    .findByEmail(email)
                    .map(ResetPasswordMapper::toDomain);
        } catch (Exception e) {
            log.error("Internal error while finding reset password by email={}", email, e);
            throw new InternalServerError("Internal error occurred while finding reset password by email", e);
        }
    }

    /**
     * Deletes the reset password record by email.
     *
     * @param email the user's email
     * @throws InternalServerError in case of unexpected error
     */
    @Override
    public void deleteByEmail(final String email) {
        try {
            resetPasswordOrm.deleteByEmail(email);
            log.info("ResetPassword deleted for email={}", email);
        } catch (Exception e) {
            log.error("Internal error while deleting reset password by email={}", email, e);
            throw new InternalServerError("Internal error occurred while deleting reset password by email", e);
        }
    }
}
