package com.gustavoventieri.framework.useCase.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.gustavoventieri.domain.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Utilitário para envio de emails utilizando templates Thymeleaf.
 * Fornece métodos para envio de emails com códigos de confirmação, redefinição
 * de senha,
 * notificações de criação de conta e alteração de senha.
 */
@Component
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final String fromEmail;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Construtor padrão.
     *
     * @param fromEmail      endereço de email remetente (configurado em
     *                       spring.mail.username)
     * @param mailSender     serviço para envio de emails
     * @param templateEngine mecanismo de templates Thymeleaf
     */
    public EmailServiceImpl(@Value("${spring.mail.username}") String fromEmail,
            JavaMailSender mailSender,
            TemplateEngine templateEngine) {
        this.fromEmail = fromEmail;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envia um email contendo código de confirmação para o endereço especificado.
     *
     * @param to   endereço de email do destinatário
     * @param code código de confirmação a ser enviado
     * @throws MessagingException caso ocorra erro no envio do email
     */
    @Override
    public void sendConfirmationCode(String to, String code) throws MessagingException {
        log.info("Enviando código de confirmação para {}", to);
        sendEmailFromTemplate(
                to,
                "Confirmação de E-mail - LinkLine",
                "confirmation-code.html",
                Map.of("code", code));
        log.info("Código de confirmação enviado para {}", to);
    }

    /**
     * Envia um email contendo código para redefinição de senha para o endereço
     * especificado.
     *
     * @param to   endereço de email do destinatário
     * @param code código de redefinição de senha
     * @throws MessagingException caso ocorra erro no envio do email
     */
    @Override
    public void sendResetPasswordCode(String to, String code) throws MessagingException {
        log.info("Enviando código de redefinição de senha para {}", to);
        sendEmailFromTemplate(
                to,
                "Redefinição de Senha - LinkLine",
                "reset-password-code.html",
                Map.of("code", code));
        log.info("Código de redefinição de senha enviado para {}", to);
    }

    /**
     * Envia um email notificando a criação de uma nova conta para o usuário
     * especificado.
     *
     * @param to       endereço de email do destinatário
     * @param username nome de usuário da conta criada
     * @throws MessagingException caso ocorra erro no envio do email
     */
    @Override
    public void sendAccountCreatedMessage(String to, String username) throws MessagingException {
        log.info("Enviando mensagem de conta criada para {}", to);
        sendEmailFromTemplate(
                to,
                "Conta Criada com Sucesso - LinkLine",
                "account-created.html",
                Map.of("username", username));
        log.info("Mensagem de conta criada enviada para {}", to);
    }

    /**
     * Envia um email notificando que a senha do usuário foi alterada com sucesso.
     *
     * @param to       endereço de email do destinatário
     * @param username nome de usuário cuja senha foi alterada
     * @throws MessagingException caso ocorra erro no envio do email
     */
    @Override
    public void sendPasswordUpdated(String to, String username) throws MessagingException {
        log.info("Enviando notificação de senha alterada para {}", to);
        sendEmailFromTemplate(
                to,
                "Senha Alterada - LinkLine",
                "password-updated.html",
                Map.of("username", username));
        log.info("Notificação de senha alterada enviada para {}", to);
    }

    /**
     * Método genérico para enviar email utilizando um template Thymeleaf.
     *
     * @param to           endereço do destinatário
     * @param subject      assunto do email
     * @param templateName nome do template Thymeleaf (arquivo HTML)
     * @param variables    mapa de variáveis para o template
     * @throws MessagingException caso ocorra erro no envio do email
     */
    private void sendEmailFromTemplate(String to, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        helper.setFrom(fromEmail);

        mailSender.send(message);
        log.debug("Email enviado para {} com assunto '{}', usando template '{}'", to, subject, templateName);
    }
}
