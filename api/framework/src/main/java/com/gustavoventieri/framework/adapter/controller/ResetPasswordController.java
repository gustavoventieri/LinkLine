package com.gustavoventieri.framework.adapter.controller;

import com.gustavoventieri.framework.adapter.dto.request.ResetPasswordRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.SendResetPasswordCodeRequestImpl;
import com.gustavoventieri.framework.adapter.dto.request.VerifyResetPasswordCodeRequestImpl;
import com.gustavoventieri.framework.useCase.service.ResetPasswordServiceImpl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo fluxo de redefinição de senha,
 * incluindo envio, reenvio, verificação de código e atualização de senha.
 * 
 * Expondo endpoints para gerenciamento completo do processo de reset de senha por e-mail.
 */
@RestController
@RequestMapping("/api/auth/reset-password")
@AllArgsConstructor
@Validated
public class ResetPasswordController {

    private final ResetPasswordServiceImpl resetPasswordServiceImpl;

    /**
     * Envia um código para redefinição de senha para o e-mail informado.
     * 
     * @param request Objeto contendo o e-mail para o qual o código será enviado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o envio do código.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordServiceImpl.sendResetPasswordCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code sent");
    }

    /**
     * Reenvia o código de redefinição de senha para o e-mail informado,
     * útil caso o usuário não tenha recebido o código anterior.
     * 
     * @param request Objeto contendo o e-mail para o qual o código será reenviado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o reenvio do código.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendResetPasswordCode(@RequestBody @Valid SendResetPasswordCodeRequestImpl request) {
        resetPasswordServiceImpl.resendResetPasswordCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code re-sent");
    }

    /**
     * Verifica se o código de redefinição de senha fornecido é válido para o e-mail informado.
     * 
     * @param request Objeto contendo o e-mail e o código a ser verificado.
     * @return ResponseEntity com status 200 OK e mensagem confirmando a validade do código.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyResetPasswordCode(@RequestBody @Valid VerifyResetPasswordCodeRequestImpl request) {
        resetPasswordServiceImpl.verifyResetPasswordCode(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.OK).body("Reset password code is valid");
    }

    /**
     * Atualiza a senha do usuário associada ao e-mail informado.
     * Normalmente é chamado após a validação do código de redefinição.
     * 
     * @param request Objeto contendo o e-mail do usuário e a nova senha.
     * @return ResponseEntity com status 200 OK e mensagem confirmando a atualização da senha.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid ResetPasswordRequestImpl request) {
        resetPasswordServiceImpl.updateUserPasswordByEmail(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body("Password successfully updated");
    }
}
