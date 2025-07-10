package org.gustavoventieri.domain.service;

public interface ResetPasswordService {


    String sendResetPasswordCode(String email);

    String resendResetPasswordCode(String email);

    void verifyResetPasswordCode(String email, String code);

    void updateUserPasswordByEmail(String email, String password);
}
