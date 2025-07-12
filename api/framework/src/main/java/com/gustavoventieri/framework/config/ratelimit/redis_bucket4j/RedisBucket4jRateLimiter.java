package com.gustavoventieri.framework.config.ratelimit.redis_bucket4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.gustavoventieri.domain.ratelimit.RateLimiter;
import org.springframework.stereotype.Component;

import com.gustavoventieri.framework.config.ratelimit.props.RateLimitProperties;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.annotation.PostConstruct;

@Component
public class RedisBucket4jRateLimiter implements RateLimiter {

    private LettuceBasedProxyManager buckets;
    private BucketConfiguration config;
    private final StatefulRedisConnection<byte[], byte[]> connection;
    private final RateLimitProperties rateLimitProperties;

    public RedisBucket4jRateLimiter(
            StatefulRedisConnection<byte[], byte[]> connection,
            RateLimitProperties rateLimitProperties) {
        this.connection = connection;
        this.rateLimitProperties = rateLimitProperties;
    }

    @PostConstruct
    public void init() {
        try {
            ExpirationAfterWriteStrategy expirationStrategy = ExpirationAfterWriteStrategy
                    .fixedTimeToLive(Duration.ofMinutes(rateLimitProperties.getExpiration()));

            this.buckets = LettuceBasedProxyManager.builderFor(connection)
                    .withExpirationStrategy(expirationStrategy)
                    .build();

            this.config = BucketConfiguration.builder()
                    .addLimit(Bandwidth.classic(
                            rateLimitProperties.getRequests(),
                            Refill.greedy(rateLimitProperties.getRequests(),
                                    Duration.ofMinutes(rateLimitProperties.getRefillInterval()))))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize RedisBucket4jRateLimiter", e);
        }
    }

    @Override
    public boolean isAllowed(String key) {
        Bucket bucket = buckets.builder()
                .build(key.getBytes(StandardCharsets.UTF_8), () -> config);
        return bucket.tryConsume(1);
    }
}
