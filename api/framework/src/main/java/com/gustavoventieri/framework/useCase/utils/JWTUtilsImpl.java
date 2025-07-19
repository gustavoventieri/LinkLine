package com.gustavoventieri.framework.useCase.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.JWTException;
import org.gustavoventieri.domain.utils.JWTUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitário para criação, validação e extração de dados de tokens JWT.
 * Utiliza a biblioteca Auth0 JWT para operações criptográficas.
 */
@Component
@Slf4j
public class JWTUtilsImpl implements JWTUtils {

    private static final String TOKEN_COOKIE_NAME = "token";

    private final Algorithm algorithm;
    private final String ISSUER;
    private final long EXPIRATION_HOURS;

    /**
     * Construtor que inicializa a chave secreta, emissor e tempo de expiração do
     * token.
     *
     * @param jwtSecret       segredo usado na assinatura HMAC256 do token
     * @param issuer          emissor do token JWT
     * @param expirationHours validade do token em horas
     */
    public JWTUtilsImpl(
            @Value("${spring.security.jwt.password}") String jwtSecret,
            @Value("${spring.security.jwt.issuer}") String issuer,
            @Value("${spring.security.jwt.expiration-hours}") long expirationHours) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.ISSUER = issuer;
        this.EXPIRATION_HOURS = expirationHours;
        log.info("JWTUtils inicializado com emissor '{}' e expiração de {} horas", issuer, expirationHours);
    }

    /**
     * Gera um token JWT para o usuário fornecido.
     *
     * @param user usuário para quem o token será gerado
     * @return token JWT assinado como String
     * @throws JWTException em caso de falha na criação do token
     */
    @Override
    public String generateUserToken(UserDomain user) {
        try {
            String token = JWT.create()
                    .withIssuer(this.ISSUER)
                    .withSubject(user.id().toString())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(this.algorithm);
            log.debug("Token JWT gerado para usuário {}", user.id());
            return token;
        } catch (JWTCreationException e) {
            log.error("Erro ao gerar token JWT para usuário {}", user.id(), e);
            throw new JWTException("Error while generating JWT token");
        }
    }

    /**
     * Valida o token JWT e extrai o ID do usuário contido nele.
     *
     * @param token token JWT a ser validado
     * @return ID do usuário como String se válido; null se inválido ou expirado
     */
    @Override
    public String validateAndExtractUserId(String token) {
        try {
            String userId = JWT.require(this.algorithm)
                    .withIssuer(this.ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            log.debug("Token JWT validado com sucesso para usuário {}", userId);
            return userId;
        } catch (JWTVerificationException e) {
            log.warn("Token JWT inválido ou expirado", e);
            return null;
        }
    }

    /**
     * Recupera o ID do usuário a partir do cookie "token" presente na requisição
     * HTTP.
     *
     * @param request requisição HTTP contendo o cookie JWT
     * @return UUID do usuário extraído do token
     * @throws JWTException se não encontrar cookie, token inválido ou expirado
     */
    @Override
    public UUID getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.error("Cookies não encontrados na requisição");
            throw new JWTException("Cookies not found");
        }

        for (Cookie cookie : cookies) {
            if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String token = cookie.getValue();
                String userId = validateAndExtractUserId(token);

                if (userId == null) {
                    log.error("Token JWT inválido ou expirado no cookie");
                    throw new JWTException("Invalid or expired token");
                }

                log.info("ID de usuário extraído do cookie: {}", userId);
                return UUID.fromString(userId);
            }
        }

        log.error("Cookie de token JWT não encontrado");
        throw new JWTException("Token not found in cookies");
    }

    /**
     * Gera a data/hora de expiração do token com base na hora atual mais o tempo
     * configurado.
     *
     * @return instante de expiração do token
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusHours(this.EXPIRATION_HOURS)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
