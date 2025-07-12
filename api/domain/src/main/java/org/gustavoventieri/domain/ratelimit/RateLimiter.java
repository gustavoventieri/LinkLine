package org.gustavoventieri.domain.ratelimit;

public interface RateLimiter {
    boolean isAllowed(String key);
}
