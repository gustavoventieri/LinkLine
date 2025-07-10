package org.gustavoventieri.domain.service;


public interface EmailVerificationService {
    
    void sendConfirmationEmailCode(String email, String username, String password);
    
    void resendConfirmationEmailCode(String email);

    void verifyConfirmationEmailCode(String email, String code);
}
