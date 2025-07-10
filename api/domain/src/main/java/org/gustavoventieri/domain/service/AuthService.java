package org.gustavoventieri.domain.service;

import java.util.Map;
import java.util.UUID;

public interface AuthService {
    String login(String email, String password);
    String register(String email, String code, String avatarUrl);
    Map<String, Object> isAuth(UUID userId);
}
