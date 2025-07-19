package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.gustavoventieri.domain.dto.response.GeneratedData;
import org.gustavoventieri.domain.entity.ResetPasswordDomain;
import org.gustavoventieri.domain.exception.Expired;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.ResetPasswordRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.EmailService;
import org.gustavoventieri.domain.service.ResetPasswordService;
import org.gustavoventieri.domain.utils.GenerateCodeUtils;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for managing the password reset flow,
 * including sending and resending recovery codes, verifying the code,
 * and updating the user's password.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final int RESET_CODE_EXPIRATION_MINUTES = 10;

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final GenerateCodeUtils generateCodeUtils;

    /**
     * Initiates the password reset process by sending a verification code
     * to the specified email address. A random code is generated with a
     * defined expiration time and sent via email.
     *
     * @param email The email of the user who requested the password reset.
     * @throws NotFound            If no user is found with the provided email.
     * @throws InternalServerError If an error occurs while sending the email.
     */
    @Override
    @Transactional
    public void initiateResetPassword(final String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("User with this email was not found."));

        final GeneratedData record = generateResetPasswordData();

        persistResetPasswordRecord(email, record.code(), record.expiresAt());

        sendResetPasswordEmail(email, record.code());

        log.info("Password reset code sent to {}", email);
    }

    /**
     * Resends a new password reset code to the specified email.
     * A reset request must already exist in order to resend the code.
     *
     * @param email The email of the user to receive the new reset code.
     * @throws NotFound            If no reset request exists for the given email.
     * @throws InternalServerError If an error occurs while sending the email.
     */
    @Override
    @Transactional
    public void resendResetPasswordCode(final String email) {
        resetPasswordRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Reset password request not found."));

        final GeneratedData record = generateResetPasswordData();

        persistResetPasswordRecord(email, record.code(), record.expiresAt());

        sendResetPasswordEmail(email, record.code());

        log.info("New password reset code re-sent to {}", email);
    }

    /**
     * Validates whether the provided reset password code is valid and not expired.
     * If the code is expired, the associated reset request will be deleted.
     *
     * @param email The email associated with the reset code.
     * @param code  The reset code to be validated.
     * @throws NotFound If the email or code is invalid.
     * @throws Expired  If the reset code has expired.
     */
    @Override
    @Transactional
    public void validateResetPasswordCode(final String email, final String code) {
        final ResetPasswordDomain record = resetPasswordRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new NotFound("Invalid reset code or email."));

        if (Instant.now().isAfter(record.expiresAt())) {
            resetPasswordRepository.deleteByEmail(email);
            log.info("Expired reset password code removed for email: {}", email);
            throw new Expired("Reset password code has expired.");
        }

        log.info("Reset password code validated for email: {}", email);
    }

    // Helpers

    /**
     * Generates a new random reset password code and calculates its expiration.
     *
     * @return GeneratedData containing the code and expiration timestamp.
     */
    private GeneratedData generateResetPasswordData() {
        final String code = generateCodeUtils.generateCode();
        final Instant expiresAt = Instant.now().plus(RESET_CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        return new GeneratedData(code, expiresAt);
    }

    /**
     * Persists or updates the password reset record in the repository.
     *
     * @param email     The email address associated with the reset request.
     * @param code      The generated reset code.
     * @param expiresAt The expiration timestamp for the reset code.
     */
    private void persistResetPasswordRecord(final String email, final String code, final Instant expiresAt) {
        resetPasswordRepository.saveOrUpdate(email, code, expiresAt);
    }

    /**
     * Sends the reset code to the user's email using the EmailService.
     *
     * @param email The recipient email address.
     * @param code  The reset password code to send.
     * @throws InternalServerError If an exception occurs during email sending.
     */
    private void sendResetPasswordEmail(final String email, final String code) {
        try {
            emailService.sendResetPasswordCode(email, code);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send reset password email", e);
        }
    }
}
