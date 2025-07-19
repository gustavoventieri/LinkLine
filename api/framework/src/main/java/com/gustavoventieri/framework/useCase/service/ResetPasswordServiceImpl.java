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
 * Serviço responsável pelo gerenciamento do fluxo de redefinição de senha,
 * incluindo envio e reenvio de códigos de recuperação, verificação do código,
 * e atualização da senha do usuário.
 */
@Service
@RequiredArgsConstructor
@Slf4j // logger
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final int RESET_CODE_EXPIRATION_MINUTES = 10;

    // Dependency Inversion Principle
    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final GenerateCodeUtils generateCodeUtils;

    /**
     * Envia um código para redefinição de senha para o e-mail informado.
     * Gera um código aleatório com validade definida e envia via e-mail.
     *
     * @param email O e-mail do usuário que solicitou a redefinição de senha.
     * @return O código gerado para redefinição.
     * @throws NotFound            Se o usuário com o e-mail informado não for
     *                             encontrado.
     * @throws InternalServerError Se ocorrer erro ao enviar o e-mail.
     */
    @Override
    @Transactional
    public void initiateResetPassword(final String email) {

        userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("User with this email was not found."));

        final GeneratedData record = generateResetPasswordData();

        persistResetPasswordRecord(email, record.code(), record.expiresAt());

        sendResetPasswordEmail(email, record.code());

        log.info("Reset password code sent to {}", email);

    }

    /**
     * Reenvia um novo código de redefinição de senha para o e-mail informado.
     * O código anterior deve existir para que seja possível reenvio.
     *
     * @param email O e-mail do usuário para o qual será enviado o novo código.
     * @return O novo código gerado para redefinição.
     * @throws NotFound            Se não existir um pedido de redefinição para o
     *                             e-mail informado.
     * @throws InternalServerError Se ocorrer erro ao enviar o e-mail.
     */
    @Override
    @Transactional
    public void resendResetPasswordCode(final String email) {

        resetPasswordRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Reset password request not found."));

        final GeneratedData record = generateResetPasswordData();

        persistResetPasswordRecord(email, record.code(), record.expiresAt());

        sendResetPasswordEmail(email, record.code());

        log.info("Reset password code re-sent to {}", email);

    }

    /**
     * Verifica se o código de redefinição de senha é válido e não expirou.
     * Se o código estiver expirado, a solicitação de redefinição será removida.
     *
     * @param email O e-mail do usuário associado ao código.
     * @param code  O código de redefinição para ser verificado.
     * @throws NotFound Se o código ou e-mail forem inválidos.
     * @throws Expired  Se o código já tiver expirado.
     */
    @Override
    @Transactional
    public void validateResetPasswordCode(final String email, final String code) {
        final ResetPasswordDomain record = resetPasswordRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new NotFound("Invalid reset code or email."));

        if (Instant.now().isAfter(record.expiresAt())) {
            resetPasswordRepository.deleteByEmail(email);
            log.info("Expired reset password code deleted for email: {}", email);
            throw new Expired("Reset password code has expired.");
        }

        log.info("Reset password code verified for email: {}", email);
    }

    // Helpers

    private GeneratedData generateResetPasswordData() {
        final String code = generateCodeUtils.generateCode();
        final Instant expiresAt = Instant.now().plus(RESET_CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        return new GeneratedData(code, expiresAt);
    }

    private void persistResetPasswordRecord(final String email, final String code, final Instant expiresAt) {
        resetPasswordRepository.saveOrUpdate(email, code, expiresAt);
    }

    private void sendResetPasswordEmail(final String email, final String code) {
        try {
            emailService.sendResetPasswordCode(email, code);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send reset password email", e);
        }
    }

}
