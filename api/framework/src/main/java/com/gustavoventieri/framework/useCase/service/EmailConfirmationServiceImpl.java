package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.gustavoventieri.domain.dto.response.GeneratedData;
import org.gustavoventieri.domain.entity.EmailConfirmationDomain;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.Expired;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.EmailConfirmationRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.EmailConfirmationService;
import org.gustavoventieri.domain.service.EmailService;
import org.gustavoventieri.domain.utils.GenerateCodeUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for the email verification process,
 * including sending and resending confirmation codes, as well as
 * validating these codes to confirm the authenticity of the email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private static final int RESET_CODE_EXPIRATION_MINUTES = 10;

    // Dependency Inversion Principle
    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;
    private final GenerateCodeUtils generateCodeUtils;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Starts the email verification process by sending a confirmation code
     * to the provided email address.
     *
     * @param email    The user's email.
     * @param username The desired username.
     * @param password The password that will be temporarily stored encoded.
     * @throws Conflict            If the username or email are already in use.
     * @throws InternalServerError If sending the email fails.
     */
    @Override
    @Transactional
    public void initiateConfirmationEmail(final String email, final String username, final String password) {
        log.info("Starting initiateEmailVerification for email: {} and username: {}", email, username);

        userRepository.findByUsername(username)
                .ifPresent(usernameIsNotAvailable -> {
                    log.warn("Username '{}' is already taken.", username);
                    throw new Conflict("This username is not available.");
                });

        userRepository.findByEmail(email)
                .ifPresent(emailIsNotAvailable -> {
                    log.warn("Email '{}' is already taken.", email);
                    throw new Conflict("This email is not available.");
                });

        final String hashedPassword = passwordEncoder.encode(password);
        final GeneratedData generatedData = generateResetPasswordData();

        persistConfirmationEmailRecord(
                email,
                username,
                hashedPassword,
                generatedData.code(),
                false,
                generatedData.expiresAt());

        sendConfirmationEmail(email, generatedData.code());

        log.info("Verification code sent successfully to {}", email);
    }

    /**
     * Resends a new confirmation code to the given email.
     *
     * @param email The email to which the code will be resent.
     * @throws NotFound            If no pending verification record exists.
     * @throws InternalServerError If sending the email fails.
     */
    @Override
    @Transactional
    public void resendConfirmationCode(final String email) {
        log.info("Starting resendVerificationCode for email: {}", email);

        final EmailConfirmationDomain record = emailConfirmationRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No verification record found for email: {}", email);
                    return new NotFound("Email not found for verification.");
                });

        final GeneratedData generatedData = generateResetPasswordData();

        persistConfirmationEmailRecord(
                record.email(),
                record.username(),
                record.password(),
                generatedData.code(),
                false,
                generatedData.expiresAt());

        sendConfirmationEmail(email, generatedData.code());

        log.info("Resent verification code to {}", email);
    }

    /**
     * Validates the verification code sent via email.
     * If the code is valid and still within the expiry period, the email is
     * confirmed.
     *
     * @param email The email to be confirmed.
     * @param code  The verification code sent to the email.
     * @throws NotFound If the code or email are invalid.
     * @throws Expired  If the code has expired.
     */
    @Override
    @Transactional
    public void validateConfirmationCode(final String email, final String code) {
        log.info("Validating verification code for email: {}", email);

        final EmailConfirmationDomain record = emailConfirmationRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> {
                    log.warn("Invalid verification code or email. Email: {}, Code: {}", email, code);
                    return new NotFound("Invalid verification code or email.");
                });

        if (Instant.now().isAfter(record.expiresAt())) {
            log.info("Verification code expired for email: {}", email);
            emailConfirmationRepository.deleteByEmail(email);
            throw new Expired("Verification code has expired.");
        }

        persistConfirmationEmailRecord(email, record.username(), record.password(), code, true, record.expiresAt());

        log.info("Verification code validated successfully for email: {}", email);
    }

    // Helpers

    private void persistConfirmationEmailRecord(
            final String email,
            final String username,
            final String password,
            final String code,
            final boolean verified,
            final Instant expiresAt) {

        emailConfirmationRepository.saveOrUpdate(email, username, password, code, verified, expiresAt);
    }

    private void sendConfirmationEmail(final String email, final String code) {
        try {
            emailService.sendConfirmationCode(email, code);
        } catch (final MessagingException e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send verification email", e);
        }
    }

    private GeneratedData generateResetPasswordData() {
        final String code = generateCodeUtils.generateCode();
        final Instant expiresAt = Instant.now().plus(RESET_CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        return new GeneratedData(code, expiresAt);
    }

}
