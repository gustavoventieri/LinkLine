package org.gustavoventieri.domain.service;

public interface ResetPasswordService {

    void initiateResetPassword(String email);

    void resendResetPasswordCode(String email);

    void validateResetPasswordCode(String email, String code);

    void updateUserPasswordByEmail( String email,  String newPassword);
}
