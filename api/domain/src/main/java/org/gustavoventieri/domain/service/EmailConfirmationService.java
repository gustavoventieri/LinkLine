package org.gustavoventieri.domain.service;

public interface EmailConfirmationService {

    void initiateConfirmationEmail(String email, String username, String password);

    void resendConfirmationCode(String email);

    void validateConfirmationCode(String email, String code);
}
