package com.gustavoventieri.framework.config.ratelimit;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gustavoventieri.framework.config.ratelimit.redis_bucket4j.RedisBucket4jRateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisBucket4jRateLimiter rateLimiter;

    private final String pathPrefix;

    public RateLimitFilter(RedisBucket4jRateLimiter rateLimiter, @Value("spring.rate-limit.path-prefix") String pathPrefix) {
        this.rateLimiter = rateLimiter;
        this.pathPrefix = pathPrefix;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith(pathPrefix)) {
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }

            // chave = IP + rota
            String key = clientIp + ":" + path;

            boolean allowed = rateLimiter.isAllowed(key);

            if (!allowed) {
                response.setStatus(429);
                response.getWriter().write("Too many requests, try again in some minutes");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


}

