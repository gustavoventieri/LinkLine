package org.gustavoventieri.domain.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendConfirmationCode(String to, String code) throws MessagingException;

    void sendResetPasswordCode(String to, String code) throws MessagingException;

    void sendAccountCreatedMessage(String to, String username) throws MessagingException;

    void sendPasswordUpdated(String to, String username) throws MessagingException;
}
