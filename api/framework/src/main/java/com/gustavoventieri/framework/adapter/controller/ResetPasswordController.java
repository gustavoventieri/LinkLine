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
 * Controller responsável pelo fluxo de redefinição de senha,
 * incluindo envio, reenvio, verificação de código e atualização de senha.
 * 
 * Expondo endpoints para gerenciamento completo do processo de reset de senha
 * por e-mail.
 */
@RestController
@RequestMapping("/api/v1/auth/reset-password")
@AllArgsConstructor
@Validated
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    /**
     * Envia um código para redefinição de senha para o e-mail informado.
     * 
     * @param request Objeto contendo o e-mail para o qual o código será enviado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o envio do
     *         código.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordService.initiateResetPassword(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code sent");
    }

    /**
     * Reenvia o código de redefinição de senha para o e-mail informado,
     * útil caso o usuário não tenha recebido o código anterior.
     * 
     * @param request Objeto contendo o e-mail para o qual o código será reenviado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o reenvio do
     *         código.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendResetPasswordCode(
            @RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordService.resendResetPasswordCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code re-sent");
    }

    /**
     * Verifica se o código de redefinição de senha fornecido é válido para o e-mail
     * informado.
     * 
     * @param request Objeto contendo o e-mail e o código a ser verificado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando a validade do
     *         código.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyResetPasswordCode(
            @RequestBody @Valid VerifyResetPasswordCodeRequestImpl request) {
        resetPasswordService.validateResetPasswordCode(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code is valid");
    }

}
