package com.gustavoventieri.framework.adapter.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.auth.LoginRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.auth.RegisterRequestImpl;
import com.gustavoventieri.framework.useCase.service.AuthServiceImpl;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsible for authentication-related endpoints,
 * such as login, registration, and token validation.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    private final JWTUtils jwtUtils;

    /**
     * Endpoint to log in a user.
     * Receives email and password, authenticates the user, and returns a JWT token
     * via cookie.
     *
     * @param loginRequestImpl Login data containing email and password.
     * @param response         HttpServletResponse used to set the cookie with the
     *                         JWT token.
     * @return HTTP response with success message.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestImpl loginRequestImpl,
            HttpServletResponse response) {
        log.info("Login attempt for email: {}", loginRequestImpl.email());

        String token = authServiceImpl.login(loginRequestImpl.email(), loginRequestImpl.password());

        setTokenCookie(response, token);

        log.info("Login successful for email: {}", loginRequestImpl.email());
        return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully");
    }

    /**
     * Endpoint to check if the user is authenticated.
     * Extracts the JWT token from the cookie, validates it, and checks if the user
     * exists.
     *
     * @param httpRequest HTTP request containing cookies.
     * @return HTTP response indicating whether the user is authenticated.
     */
    @GetMapping("/isAuth")
    public ResponseEntity<Map<String, Object>> isAuth(HttpServletRequest httpRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpRequest);
        log.info("Checking authentication for user ID: {}", userId);

        Map<String, Object> response = authServiceImpl.isAuth(userId); // throws exception if not found or invalid

        log.info("User is authenticated. ID: {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to register a new user.
     * Validates the verification code, creates the user, and returns a JWT token
     * via cookie.
     *
     * @param registerRequestImpl Registration data containing email, code, and
     *                            avatar URL.
     * @param response            HttpServletResponse used to set the cookie with
     *                            the JWT token.
     * @return HTTP response with success message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestImpl registerRequestImpl,
            HttpServletResponse response) {
        log.info("Registration attempt for email: {}", registerRequestImpl.email());

        String token = authServiceImpl.register(
                registerRequestImpl.email(),
                registerRequestImpl.code(),
                registerRequestImpl.avatarUrl());

        setTokenCookie(response, token);

        log.info("Registration successful for email: {}", registerRequestImpl.email());
        return ResponseEntity.status(HttpStatus.OK).body("User registered successfully");
    }

    /**
     * Helper method to configure the HTTP cookie with the JWT token.
     *
     * @param response HttpServletResponse to which the cookie will be added.
     * @param token    JWT token to be stored in the cookie.
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // In production, set to true for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(cookie);
    }
}
