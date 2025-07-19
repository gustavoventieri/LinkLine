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

@Component
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final String fromEmail;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(final @Value("${spring.mail.username}") String fromEmail,
                            final JavaMailSender mailSender,
                            final TemplateEngine templateEngine) {
        this.fromEmail = fromEmail;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendConfirmationCode(final String to, final String code) throws MessagingException {
        log.info("Enviando código de confirmação para {}", to);
        sendEmailFromTemplate(
                to,
                "Confirmação de E-mail - LinkLine",
                "confirmation-code.html",
                Map.of("code", code));
        log.info("Código de confirmação enviado para {}", to);
    }

    @Override
    public void sendResetPasswordCode(final String to, final String code) throws MessagingException {
        log.info("Enviando código de redefinição de senha para {}", to);
        sendEmailFromTemplate(
                to,
                "Redefinição de Senha - LinkLine",
                "reset-password-code.html",
                Map.of("code", code));
        log.info("Código de redefinição de senha enviado para {}", to);
    }

    @Override
    public void sendAccountCreatedMessage(final String to, final String username) throws MessagingException {
        log.info("Enviando mensagem de conta criada para {}", to);
        sendEmailFromTemplate(
                to,
                "Conta Criada com Sucesso - LinkLine",
                "account-created.html",
                Map.of("username", username));
        log.info("Mensagem de conta criada enviada para {}", to);
    }

    @Override
    public void sendPasswordUpdated(final String to, final String username) throws MessagingException {
        log.info("Enviando notificação de senha alterada para {}", to);
        sendEmailFromTemplate(
                to,
                "Senha Alterada - LinkLine",
                "password-updated.html",
                Map.of("username", username));
        log.info("Notificação de senha alterada enviada para {}", to);
    }

    private void sendEmailFromTemplate(final String to,
                                       final String subject,
                                       final String templateName,
                                       final Map<String, Object> variables) throws MessagingException {
        final MimeMessage message = mailSender.createMimeMessage();

        final MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        final Context context = new Context();
        context.setVariables(variables);

        final String html = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        helper.setFrom(fromEmail);

        mailSender.send(message);
        log.debug("Email enviado para {} com assunto '{}', usando template '{}'", to, subject, templateName);
    }
}
