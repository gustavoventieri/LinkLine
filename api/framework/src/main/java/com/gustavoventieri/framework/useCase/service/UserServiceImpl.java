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
     * Updates the password of the user associated with the provided email.
     * This method is only allowed if there is a valid and non-expired reset password request.
     * After the update, the reset request is removed and the user is notified by email.
     *
     * @param email       The email of the user whose password will be updated.
     * @param newPassword The new password in plain text.
     * @throws IllegalArgumentException If the new password is null or empty.
     * @throws NotFound                 If the reset request or user is not found.
     * @throws Expired                  If the reset request has expired.
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

        log.info("Password successfully updated for email: {}. Reset password record removed.", email);
    }

    /**
     * Sends a confirmation email to notify the user that the password has been updated.
     *
     * @param email    The email of the user.
     * @param username The username of the user.
     * @throws InternalServerError If there is an error while sending the email.
     */
    private void sendResetPasswordEmail(final String email, final String username) {
        try {
            emailService.sendPasswordUpdated(email, username);
        } catch (MessagingException e) {
            log.error("Failed to send password update notification to {}: {}", email, e.getMessage(), e);
            throw new InternalServerError("Failed to send password update email", e);
        }
    }
}
