package com.gustavoventieri.framework.useCase.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PotentialFriendResponse;
import org.gustavoventieri.domain.entity.ResetPasswordDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.Expired;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.ResetPasswordRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.EmailService;
import org.gustavoventieri.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<PotentialFriendResponse> findUsersByUsername(String searchTerm, UUID currentUserId, int searchLimit) {

        throw new UnsupportedOperationException("Unimplemented method 'findUsersByUsername'");
    }

    /**
     * Atualiza a senha do usuário associado ao e-mail informado.
     * A senha é atualizada somente se o código de redefinição for válido e não
     * expirado.
     * Após atualização, a solicitação de redefinição é removida e o usuário é
     * notificado por e-mail.
     *
     * @param email       O e-mail do usuário que terá a senha atualizada.
     * @param newPassword A nova senha em texto puro.
     * @throws IllegalArgumentException Se a nova senha for nula ou vazia.
     * @throws NotFound                 Se a solicitação de redefinição ou usuário
     *                                  não forem encontrados.
     * @throws Expired                  Se o código de redefinição estiver expirado.
     */
    @Override
    @Transactional
    public void updateUserPasswordByEmail(final String email, final String newPassword) {

        final ResetPasswordDomain record = resetPasswordRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Reset request not found."));

        if (Instant.now().isAfter(record.expiresAt())) {
            resetPasswordRepository.deleteByEmail(email);
            log.info("Expired reset password code deleted for email: {}", email);
            throw new Expired("Reset password code has expired.");
        }

        final UserDomain user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("User with this email not found."));

        userRepository.updatePasswordByEmail(email, passwordEncoder.encode(newPassword));

        resetPasswordRepository.deleteByEmail(email);

        sendResetPasswordEmail(email, user.username());

        log.info("User password updated and reset token deleted for email: {}", email);

    }

    private void sendResetPasswordEmail(final String email, final String username) {
        try {
            emailService.sendPasswordUpdated(email, username);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send reset password email", e);
        }
    }
}
