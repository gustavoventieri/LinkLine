package com.gustavoventieri.framework.config.ratelimit.redis_bucket4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.gustavoventieri.domain.ratelimit.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private final int requests;
    private final int refillInterval;
    private final int expiration;

    public RedisBucket4jRateLimiter(
            StatefulRedisConnection<byte[], byte[]> connection,
            @Value("${spring.rate-limit.requests}") int requests,
            @Value("${spring.rate-limit.refil-interval}") int refillInterval,
            @Value("${spring.rate-limit.expiration}") int expiration) {
        this.connection = connection;
        this.requests = requests;
        this.refillInterval = refillInterval;
        this.expiration = expiration;
    }

    /**
     * Permite até 10 requisições a cada 15 minutos por chave (IP).
     * Expira o estado do bucket após 10 minutos de inatividade.
     */

    @PostConstruct
    public void init() {
        try {
            ExpirationAfterWriteStrategy expirationStrategy = ExpirationAfterWriteStrategy
                    .fixedTimeToLive(Duration.ofMinutes(expiration));

            this.buckets = LettuceBasedProxyManager.builderFor(connection)
                    .withExpirationStrategy(expirationStrategy)
                    .build();

            this.config = BucketConfiguration.builder()
                    .addLimit(Bandwidth.classic(requests, Refill.greedy(requests, Duration.ofMinutes(refillInterval))))
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
