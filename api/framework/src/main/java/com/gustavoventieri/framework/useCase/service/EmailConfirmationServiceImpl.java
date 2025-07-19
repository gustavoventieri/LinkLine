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
 * Serviço responsável pelo processo de verificação de e-mail,
 * incluindo envio e reenvio de códigos de confirmação, bem como
 * a validação desses códigos para confirmar a autenticidade do e-mail.
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
     * Inicia o processo de verificação de e-mail enviando um código de confirmação
     * ao endereço fornecido.
     *
     * @param email    O e-mail do usuário.
     * @param username O nome de usuário desejado.
     * @param password A senha que será armazenada temporariamente codificada.
     * @throws Conflict            Se o nome de usuário ou e-mail já estiverem em
     *                             uso.
     * @throws InternalServerError Se falhar ao enviar o e-mail.
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
     * Reenvia um novo código de confirmação para o e-mail informado.
     *
     * @param email O e-mail para o qual será reenviado o código.
     * @throws NotFound            Se não existir um registro pendente de
     *                             verificação.
     * @throws InternalServerError Se falhar ao enviar o e-mail.
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
     * Valida o código de verificação enviado por e-mail.
     * Se o código for válido e ainda estiver dentro do prazo, o e-mail é
     * confirmado.
     *
     * @param email O e-mail a ser confirmado.
     * @param code  O código de verificação enviado para o e-mail.
     * @throws NotFound Se o código ou e-mail forem inválidos.
     * @throws Expired  Se o código tiver expirado.
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
