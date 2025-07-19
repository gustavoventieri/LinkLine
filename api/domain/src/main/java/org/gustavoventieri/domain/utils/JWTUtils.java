package org.gustavoventieri.domain.utils;

import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

import org.gustavoventieri.domain.entity.UserDomain;


import org.gustavoventieri.domain.exception.JWTException;

public interface JWTUtils {

    String generateUserToken(UserDomain user);

    String validateAndExtractUserId(String token);

    UUID getUserIdFromCookie(HttpServletRequest request) throws JWTException;
}
