package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.gustavoventieri.domain.entity.ResetPasswordDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.Expired;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.service.ResetPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.gustavoventieri.framework.driver.repository.ResetPasswordRepositoryImpl;
import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;
import com.gustavoventieri.framework.useCase.utils.EmailUtils;
import com.gustavoventieri.framework.useCase.utils.GenerateCodeUtils;

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

    private final ResetPasswordRepositoryImpl resetPasswordRepositoryImpl;
    private final UserRepositoryImpl userRepositoryImpl;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * Envia um código para redefinição de senha para o e-mail informado.
     * Gera um código aleatório com validade definida e envia via e-mail.
     *
     * @param email O e-mail do usuário que solicitou a redefinição de senha.
     * @return O código gerado para redefinição.
     * @throws NotFound Se o usuário com o e-mail informado não for encontrado.
     * @throws InternalServerError Se ocorrer erro ao enviar o e-mail.
     */
    @Override
    @Transactional
    public String sendResetPasswordCode(String email) {
        try {
            userRepositoryImpl.findByEmail(email)
                .orElseThrow(() -> new NotFound("User with this email was not found."));

            String code = GenerateCodeUtils.generateCode();
            Instant expiresAt = Instant.now().plus(RESET_CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

            resetPasswordRepositoryImpl.saveOrUpdate(email, code, expiresAt);

            emailUtils.sendResetPasswordCode(email, code);
            log.info("Reset password code sent to {}", email);

            return code;
        } catch (MessagingException e) {
            log.error("Failed to send reset password code email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send reset password code email.", e);
        }
    }

    /**
     * Reenvia um novo código de redefinição de senha para o e-mail informado.
     * O código anterior deve existir para que seja possível reenvio.
     *
     * @param email O e-mail do usuário para o qual será enviado o novo código.
     * @return O novo código gerado para redefinição.
     * @throws NotFound Se não existir um pedido de redefinição para o e-mail informado.
     * @throws InternalServerError Se ocorrer erro ao enviar o e-mail.
     */
    @Override
    @Transactional
    public String resendResetPasswordCode(String email) {
        try {
            resetPasswordRepositoryImpl.findByEmail(email)
                .orElseThrow(() -> new NotFound("Reset password request not found."));

            String newCode = GenerateCodeUtils.generateCode();
            Instant expiresAt = Instant.now().plus(RESET_CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

            resetPasswordRepositoryImpl.saveOrUpdate(email, newCode, expiresAt);

            emailUtils.sendResetPasswordCode(email, newCode);
            log.info("Reset password code re-sent to {}", email);

            return newCode;
        } catch (MessagingException e) {
            log.error("Failed to resend reset password code email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to resend reset password code email.", e);
        }
    }

    /**
     * Verifica se o código de redefinição de senha é válido e não expirou.
     * Se o código estiver expirado, a solicitação de redefinição será removida.
     *
     * @param email O e-mail do usuário associado ao código.
     * @param code O código de redefinição para ser verificado.
     * @throws NotFound Se o código ou e-mail forem inválidos.
     * @throws Expired Se o código já tiver expirado.
     */
    @Override
    @Transactional
    public void verifyResetPasswordCode(String email, String code) {
        ResetPasswordDomain record = resetPasswordRepositoryImpl.findByEmailAndCode(email, code)
            .orElseThrow(() -> new NotFound("Invalid reset code or email."));

        if (Instant.now().isAfter(record.expiresAt())) {
            resetPasswordRepositoryImpl.deleteByEmail(email);
            log.info("Expired reset password code deleted for email: {}", email);
            throw new Expired("Reset password code has expired.");
        }

        log.info("Reset password code verified for email: {}", email);
    }

    /**
     * Atualiza a senha do usuário associado ao e-mail informado.
     * A senha é atualizada somente se o código de redefinição for válido e não expirado.
     * Após atualização, a solicitação de redefinição é removida e o usuário é notificado por e-mail.
     *
     * @param email O e-mail do usuário que terá a senha atualizada.
     * @param newPassword A nova senha em texto puro.
     * @throws IllegalArgumentException Se a nova senha for nula ou vazia.
     * @throws NotFound Se a solicitação de redefinição ou usuário não forem encontrados.
     * @throws Expired Se o código de redefinição estiver expirado.
     */
    @Override
    @Transactional
    public void updateUserPasswordByEmail(String email, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be empty");
        }

        ResetPasswordDomain record = resetPasswordRepositoryImpl.findByEmail(email)
            .orElseThrow(() -> new NotFound("Reset request not found."));

        if (Instant.now().isAfter(record.expiresAt())) {
            resetPasswordRepositoryImpl.deleteByEmail(email);
            log.info("Expired reset password code deleted for email: {}", email);
            throw new Expired("Reset password code has expired.");
        }

        UserDomain user = userRepositoryImpl.findByEmail(email)
            .orElseThrow(() -> new NotFound("User with this email not found."));

        
        userRepositoryImpl.updatePasswordByEmail(email, passwordEncoder.encode(newPassword));

        resetPasswordRepositoryImpl.deleteByEmail(email);
        log.info("User password updated and reset token deleted for email: {}", email);

        try {
            emailUtils.sendPasswordUpdated(email, user.username());
            log.info("Password updated notification sent to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send password updated email to {}: {}", email, e.getMessage(), e);
        }
    }
}
