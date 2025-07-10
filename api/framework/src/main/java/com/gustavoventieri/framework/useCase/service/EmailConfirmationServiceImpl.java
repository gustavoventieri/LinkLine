package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.Expired;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.service.EmailVerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gustavoventieri.framework.adapter.mapper.EmailVerificationMapper;
import com.gustavoventieri.framework.driver.repository.EmailConfirmationRepositoryImpl;
import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;
import com.gustavoventieri.framework.entity.EmailConfirmation;
import com.gustavoventieri.framework.useCase.utils.EmailUtils;
import com.gustavoventieri.framework.useCase.utils.GenerateCodeUtils;

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
public class EmailConfirmationServiceImpl implements EmailVerificationService {

    

    private final EmailConfirmationRepositoryImpl verificationRepositoryImpl;
    private final UserRepositoryImpl userRepositoryImpl;
    private final EmailUtils emailUtils;
    private final PasswordEncoder encryptionUtils;

    @Override
    @Transactional
    public void sendConfirmationEmailCode(String email, String username, String password) {
        log.info("Starting sendConfirmationEmailCode for email: {} and username: {}", email, username);
        try {
            if (userRepositoryImpl.findByUsername(username).isPresent()) {
                log.warn("Username '{}' is already taken.", username);
                throw new Conflict("This username is not available.");
            }

            if (userRepositoryImpl.findByEmail(email).isPresent()) {
                log.warn("Email '{}' is already taken.", email);
                throw new Conflict("This email is not available.");
            }

            String code = GenerateCodeUtils.generateCode();

            String hashedPassword = encryptionUtils.encode(password);

            Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

            verificationRepositoryImpl.saveOrUpdate(email, username, hashedPassword, code, false, expiresAt);

            emailUtils.sendConfirmationCode(email, code);

            log.info("Confirmation email code sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send confirmation email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send confirmation email", e);
        }
    }

    @Override
    @Transactional
    public void resendConfirmationEmailCode(String email) {
        log.info("Starting resendConfirmationEmailCode for email: {}", email);
        try {
            EmailConfirmationDomain emailVerificationRecord = verificationRepositoryImpl.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No verification record found for email: {}", email);
                    return new NotFound("Email not found for verification.");
                });

            String newCode = GenerateCodeUtils.generateCode();

            Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

            EmailConfirmation entity = EmailVerificationMapper.toEntity(emailVerificationRecord);

            verificationRepositoryImpl.saveOrUpdate(
                entity.getEmail(),
                entity.getUsername(),
                entity.getPassword(),
                newCode,
                false,
                expiresAt
            );

            emailUtils.sendConfirmationCode(email, newCode);

            log.info("Resent confirmation email code to {}", email);


        } catch (MessagingException e) {
            log.error("Failed to resend confirmation email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to resend confirmation email", e);
        }
    }

    @Override
    @Transactional
    public void verifyConfirmationEmailCode(String email, String code) {
        log.info("Verifying confirmation email code for email: {}", email);

        EmailConfirmationDomain emailVerificationDomain = verificationRepositoryImpl.findByEmailAndCode(email, code)
            .orElseThrow(() -> {
                log.warn("Invalid verification code or email. Email: {}, Code: {}", email, code);
                return new NotFound("Invalid verification code or email.");
            });

        if (Instant.now().isAfter(emailVerificationDomain.expiresAt())) {
            log.info("Verification code expired for email: {}", email);
            verificationRepositoryImpl.deleteByEmail(email);
            throw new Expired("Verification code has expired.");
        }

        verificationRepositoryImpl.saveOrUpdate(email, emailVerificationDomain.username(), emailVerificationDomain.password(), code, true, emailVerificationDomain.expiresAt());


        log.info("Verification code validated successfully for email: {}", email);
    }
}
