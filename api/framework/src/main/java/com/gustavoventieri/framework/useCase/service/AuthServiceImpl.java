package com.gustavoventieri.framework.useCase.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.gustavoventieri.domain.entity.EmailConfirmationDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.InvalidData;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.exception.Unauthorized;
import org.gustavoventieri.domain.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.driver.repository.EmailConfirmationRepositoryImpl;
import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;
import com.gustavoventieri.framework.entity.User;
import com.gustavoventieri.framework.useCase.utils.EmailUtils;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;

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


    private final EmailConfirmationRepositoryImpl verificationRepositoryImpl;
    private final UserRepositoryImpl userRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtils emaiUtils;
    private final JWTUtils jwtUtils;

    /**
     * Realiza o login do usuário autenticando email e senha.
     *
     * @param email    o email do usuário
     * @param password a senha fornecida
     * @return token JWT para acesso autorizado
     * @throws NotFound       se o usuário não for encontrado
     * @throws InvalidData    se a senha estiver incorreta
     * @throws InternalServerError se o token JWT não puder ser gerado corretamente
     */
    @Override
    public String login(String email, String password) {
        log.info("Attempting login for email: {}", email);

        UserDomain user = this.userRepositoryImpl.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new NotFound("User not found");
                });

        boolean passwordMatched = passwordEncoder.matches(password, user.password());

        if (!passwordMatched) {
            log.warn("Invalid password attempt for user: {}", email);
            throw new NotFound("Invalid credentials");
        }

        String token = jwtUtils.generateUserToken(UserMapper.toEntityBasic(user));

        if (token == null || token.isBlank()) {
            log.error("Failed to generate JWT token for user: {}", email);
            throw new InternalServerError("Invalid token");
        }

        log.info("Login successful for email: {}", email);
        return token;
    }

    /**
     * Registra um novo usuário após validar o código de verificação enviado por email.
     *
     * @param email     o email do usuário
     * @param code      o código de verificação recebido
     * @param avatarUrl URL do avatar do usuário
     * @return token JWT para acesso autorizado após registro
     * @throws Unauthorized        se o código de verificação for inválido
     * @throws InternalServerError se o envio do email de confirmação falhar
     */
    @Override
    @Transactional
    public String register(String email, String code, String avatarUrl) {
        log.info("Starting registration for email: {}", email);

        EmailConfirmationDomain emailVerificationRecord = verificationRepositoryImpl.findByEmailAndCode(email, code)
        .filter(EmailConfirmationDomain::verified)
        .orElseThrow(() -> {
            log.warn("Invalid verification attempt. Email: {}, Code: {}", email, code);
            return new Unauthorized("Invalid verification code or email.");
        });
   

        User newUser = new User(
            null,
            emailVerificationRecord.username(),
            emailVerificationRecord.email(),
            emailVerificationRecord.password(),
            avatarUrl,
            LocalDateTime.now(),
            LocalDateTime.now(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()

        );

        UserDomain savedUser = userRepositoryImpl.save(UserMapper.toDomainComplete(newUser));
        verificationRepositoryImpl.deleteByEmail(email);

        try {
            emaiUtils.sendAccountCreatedMessage(savedUser.email(), savedUser.username());
            log.info("Confirmation email sent to: {}", savedUser.email());
        } catch (MessagingException e) {
            log.error("Failed to send confirmation email to: {}", savedUser.email(), e);
            throw new InternalServerError("Failed to send confirmation email", e);
        }

        String token = jwtUtils.generateUserToken(UserMapper.toEntityBasic(savedUser));
        log.info("Registration successful for email: {}", email);
        return token;
    }

   @Override
    public Map<String, Object> isAuth(UUID userId) {
        userRepositoryImpl.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found or not authenticated. userId: {}", userId);
                return new Unauthorized("User not authenticated");
            });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User is authenticated");
        response.put("userId", userId);

        return response;
    }

}
