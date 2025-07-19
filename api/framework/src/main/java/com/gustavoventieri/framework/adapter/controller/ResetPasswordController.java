package com.gustavoventieri.framework.adapter.controller;

import org.gustavoventieri.domain.service.ResetPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.auth.reset_password.SendResetPasswordCodeRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.auth.reset_password.VerifyResetPasswordCodeRequestImpl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * Controller responsible for the password reset flow,
 * including sending, resending, verifying code, and updating password.
 * 
 * Exposes endpoints to fully manage the email-based password reset process.
 */
@RestController
@RequestMapping("/api/v1/auth/reset-password")
@AllArgsConstructor
@Validated
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    /**
     * Sends a password reset code to the provided email address.
     *
     * @param request Object containing the email to which the code will be sent.
     * @return ResponseEntity with 200 OK and a message confirming the code was
     *         sent.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordService.initiateResetPassword(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code sent");
    }

    /**
     * Resends the password reset code to the provided email,
     * useful in case the user did not receive the previous one.
     *
     * @param request Object containing the email to which the code will be resent.
     * @return ResponseEntity with 200 OK and a message confirming the code was
     *         resent.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendResetPasswordCode(
            @RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordService.resendResetPasswordCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code re-sent");
    }

    /**
     * Verifies if the provided password reset code is valid for the given email.
     *
     * @param request Object containing the email and the code to verify.
     * @return ResponseEntity with 200 OK and a message confirming the code is
     *         valid.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyResetPasswordCode(
            @RequestBody @Valid VerifyResetPasswordCodeRequestImpl request) {
        resetPasswordService.validateResetPasswordCode(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code is valid");
    }
}
