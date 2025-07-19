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

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@Validated
public class UserController {
        private final UserRepository userRepository;
        /**
         * Atualiza a senha do usuário associada ao e-mail informado.
         * Normalmente é chamado após a validação do código de redefinição.
         * 
         * @param request Objeto contendo o e-mail do usuário e a nova senha.
         * @return ResponseEntity com status 200 OK e mensagem confirmando a atualização
         *         da senha.
         */
        @PutMapping("/update")
        public ResponseEntity<String> updatePassword(@RequestBody @Valid ResetPasswordRequestImpl request) {
                userRepository.updatePasswordByEmail(request.email(), request.password());
                return ResponseEntity.status(HttpStatus.OK).body("Password successfully updated");
        }
}
