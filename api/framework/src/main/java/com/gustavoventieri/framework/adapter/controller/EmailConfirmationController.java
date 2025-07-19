package com.gustavoventieri.framework.adapter.controller;

import org.gustavoventieri.domain.service.EmailConfirmationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.auth.email_confirmation.ResendConfirmationEmailCodeRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.auth.email_confirmation.SendConfirmationEmailCodeRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.auth.email_confirmation.VerifyConfirmationEmailCodeRequestImpl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsible for managing the flow of sending and verifying
 * email confirmation codes during the authentication process.
 *
 * Exposes endpoints for initial code sending, resending, and verification.
 */
@RestController
@RequestMapping("/api/v1/auth/email-confirmation")
@AllArgsConstructor
@Slf4j
@Validated
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    /**
     * Sends a confirmation code to the specified email, typically used during
     * new user registration.
     *
     * @param request Object containing the user's email, username, and password for
     *                registration.
     * @return ResponseEntity with status 200 OK and a message confirming the code
     *         was sent.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendConfirmationCode(
            @RequestBody @Valid SendConfirmationEmailCodeRequestImpl request) {
        log.info("Sending confirmation code to email: {}", request.email());
        emailConfirmationService.initiateConfirmationEmail(request.email(), request.username(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body("Verification Email Sent");
    }

    /**
     * Resends a confirmation code to the specified email.
     * Useful if the user did not receive or lost the previous code.
     *
     * @param request Object containing the email for resending the code.
     * @return ResponseEntity with status 200 OK and a message confirming the code
     *         was resent.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendConfirmationCode(
            @RequestBody @Valid ResendConfirmationEmailCodeRequestImpl request) {
        log.info("Resending confirmation code to email: {}", request.email());
        emailConfirmationService.resendConfirmationCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Verification Email Re-Sent");
    }

    /**
     * Verifies whether the provided confirmation code matches the one sent to the
     * user.
     * This verification typically allows the user to proceed with registration or
     * authentication.
     *
     * @param request Object containing the email and code to be validated.
     * @return ResponseEntity with status 200 OK and a message confirming the
     *         verification.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyConfirmationCode(
            @RequestBody @Valid VerifyConfirmationEmailCodeRequestImpl request) {
        log.info("Verifying confirmation code for email: {}", request.email());
        emailConfirmationService.validateConfirmationCode(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.OK).body("Email Verified");
    }
}
