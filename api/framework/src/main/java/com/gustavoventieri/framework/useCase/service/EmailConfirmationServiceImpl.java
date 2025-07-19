package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
 * Serviço responsável pelo processo de verificação de e-mail,
 * incluindo envio e reenvio de códigos de confirmação, bem como
 * a validação desses códigos para confirmar a autenticidade do e-mail.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    // Dependency Inversion Principle
    private final EmailConfirmationRepository verificationRepository;
    private final UserRepository userRepository;
    private final GenerateCodeUtils generateCodeUtils;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void initiateConfirmation(String email, String username, String password) {
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

        String verificationCode = generateCodeUtils.generateCode();
        String hashedPassword = passwordEncoder.encode(password);
        Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

        persistVerificationRecord(email, username, hashedPassword, verificationCode, false, expiresAt);
        sendVerificationCodeEmail(email, verificationCode);

        log.info("Verification code sent successfully to {}", email);

    }

    @Override
    @Transactional
    public void resendConfirmationCode(String email) {
        log.info("Starting resendVerificationCode for email: {}", email);

        EmailConfirmationDomain record = verificationRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No verification record found for email: {}", email);
                    return new NotFound("Email not found for verification.");
                });

        String newVerificationCode = generateCodeUtils.generateCode();

        Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

        persistVerificationRecord(
                record.email(),
                record.username(),
                record.password(),
                newVerificationCode,
                false,
                expiresAt);

        sendVerificationCodeEmail(email, newVerificationCode);

        log.info("Resent verification code to {}", email);

    }

    @Override
    @Transactional
    public void validateConfirmationCode(String email, String code) {
        log.info("Validating verification code for email: {}", email);

        EmailConfirmationDomain record = verificationRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> {
                    log.warn("Invalid verification code or email. Email: {}, Code: {}", email, code);
                    return new NotFound("Invalid verification code or email.");
                });

        if (Instant.now().isAfter(record.expiresAt())) {
            log.info("Verification code expired for email: {}", email);
            verificationRepository.deleteByEmail(email);
            throw new Expired("Verification code has expired.");
        }

        persistVerificationRecord(email, record.username(), record.password(), code, true, record.expiresAt());

        log.info("Verification code validated successfully for email: {}", email);
    }

    private void persistVerificationRecord(String email, String username, String password, String code,
            boolean verified,
            Instant expiresAt) {
        verificationRepository.saveOrUpdate(email, username, password, code, verified, expiresAt);
    }

    private void sendVerificationCodeEmail(String email, String code) {
        try {
            emailService.sendConfirmationCode(email, code);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send verification email", e);
        }
    }
}
