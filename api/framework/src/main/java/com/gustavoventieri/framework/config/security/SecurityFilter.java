package com.gustavoventieri.framework.config.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
    

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        if (token != null) {
            try {
                String userId = jwtUtils.validateAndExtractUserId(token);
                UUID userIdToUUID = UUID.fromString(userId);
                
                if (userId != null) {
                    
                    Optional<UserDomain> user = userRepositoryImpl.findById(userIdToUUID);

                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

   

        private String recoverToken(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();

            if (cookies == null) return null;

            return Arrays.stream(cookies)
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

}