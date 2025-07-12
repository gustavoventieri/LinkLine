package com.gustavoventieri.framework.config.ratelimit.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.rate-limit")
public class RateLimitProperties {
    private int requests;
    private int refillInterval;
    private int expiration;
}
