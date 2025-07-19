package com.gustavoventieri.framework.useCase.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.exception.Unauthorized;
import org.gustavoventieri.domain.repository.EmailConfirmationRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.AuthService;
import org.gustavoventieri.domain.service.EmailService;
import org.gustavoventieri.domain.utils.JWTUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.entity.User;

import jakarta.mail.MessagingException;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de autenticação responsável pelo login e
 * registro de usuários, incluindo validação de credenciais, verificação de
 * código de email e envio de mensagens de confirmação.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    // Dependency Inversion Principle
    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;
    
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String email, String password) {
        log.info("Attempting login for email: {}", email);

        UserDomain record = this.userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new NotFound("Invalid credentials");
                });

        boolean passwordMatched = passwordEncoder.matches(password, record.password());

        if (!passwordMatched) {
            log.warn("Invalid password attempt for user: {}", email);
            throw new NotFound("Invalid credentials");
        }

        String token = generateToken(record);

        log.info("Login successful for email: {}", email);
        return token;
    }

    @Override
    @Transactional
    public String register(String email, String code, String avatarUrl) {
        log.info("Starting registration for email: {}", email);

        EmailConfirmationDomain record = emailConfirmationRepository.findByEmailAndCode(email, code)
                .filter(EmailConfirmationDomain::verified)
                .orElseThrow(() -> {
                    log.warn("Invalid verification attempt. Email: {}, Code: {}", email, code);
                    return new Unauthorized("Invalid verification code or email.");
                });

        User user = new User(
                null,
                record.username(),
                record.email(),
                record.password(),
                avatarUrl,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>());

        UserDomain savedUser = userRepository.save(UserMapper.toDomainComplete(user));
        sendConfirmationEmailOrThrow(savedUser);
        emailConfirmationRepository.deleteByEmail(email);

        String token = generateToken(savedUser);
        log.info("Registration successful for email: {}", email);
        return token;
    }

    @Override
    public Map<String, Object> isAuth(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found or not authenticated. userId: {}", userId);
                    return new Unauthorized("User not authenticated");
                });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User is authenticated");
        response.put("userId", userId);

        return response;
    }

    private void sendConfirmationEmailOrThrow(UserDomain user) {
        try {
            emailService.sendAccountCreatedMessage(user.email(), user.username());
            log.info("Confirmation email sent to: {}", user.email());
        } catch (MessagingException e) {
            log.error("Failed to send confirmation email to: {}", user.email(), e);
            throw new InternalServerError("Failed to send confirmation email", e);
        }
    }

    private String generateToken(UserDomain user) {
        String token = jwtUtils.generateUserToken(user);
        if (token == null || token.isBlank()) {
            throw new InternalServerError("Invalid token");
        }
        return token;
    }
}
