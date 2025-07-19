package com.gustavoventieri.framework.adapter.controller;

import org.gustavoventieri.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.auth.reset_password.ResetPasswordRequestImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsible for user-related actions, such as updating password.
 */
@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@Validated
public class UserController {

        private final UserRepository userRepository;

        /**
         * Updates the password for the user associated with the provided email.
         * Typically called after a successful password reset code validation.
         *
         * @param request Object containing the user's email and the new password.
         * @return ResponseEntity with status 200 OK and a message confirming the
         *         password update.
         */
        @PutMapping("/update")
        public ResponseEntity<String> updatePassword(@RequestBody @Valid ResetPasswordRequestImpl request) {
                userRepository.updatePasswordByEmail(request.email(), request.password());
                return ResponseEntity.status(HttpStatus.OK).body("Password successfully updated");
        }
}
