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
 * Controller responsável por gerenciar o fluxo de envio e verificação
 * de códigos de confirmação por e-mail durante o processo de autenticação.
 * 
 * Expõe endpoints para envio inicial, reenvio e verificação de códigos.
 */
@RestController
@RequestMapping("/api/v1/auth/email-confirmation")
@AllArgsConstructor
@Slf4j
@Validated
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    /**
     * Envia um código de confirmação para o e-mail informado,
     * geralmente utilizado durante o cadastro de um novo usuário.
     * 
     * @param request Objeto que contém o e-mail, nome do usuário e senha para
     *                cadastro.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o envio do
     *         código.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendConfirmationCode(
            @RequestBody @Valid SendConfirmationEmailCodeRequestImpl request) {
        log.info("Enviando código de confirmação para o email: {}", request.email());
        emailConfirmationService.initiateConfirmationEmail(request.email(), request.username(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body("Verification Email Sent");
    }

    /**
     * Reenvia o código de confirmação para o e-mail informado.
     * Útil para casos onde o usuário não recebeu ou perdeu o código anterior.
     * 
     * @param request Objeto contendo o e-mail para reenvio do código.
     * @return ResponseEntity com status 200 OK e mensagem confirmando o reenvio.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendConfirmationCode(
            @RequestBody @Valid ResendConfirmationEmailCodeRequestImpl request) {
        log.info("Reenviando código de confirmação para o email: {}", request.email());
        emailConfirmationService.resendConfirmationCode(request.email());
        return ResponseEntity.status(HttpStatus.OK).body("Verification Email Re-Sent");
    }

    /**
     * Verifica se o código de confirmação enviado corresponde ao código recebido
     * pelo usuário.
     * Essa verificação geralmente libera o prosseguimento do cadastro ou
     * autenticação.
     * 
     * @param request Objeto contendo o e-mail e o código para validação.
     * @return ResponseEntity com status 200 OK e mensagem confirmando a
     *         verificação.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyConfirmationCode(
            @RequestBody @Valid VerifyConfirmationEmailCodeRequestImpl request) {
        log.info("Verificando código de confirmação para o email: {}", request.email());
        emailConfirmationService.validateConfirmationCode(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.OK).body("Email Verified");
    }
}
