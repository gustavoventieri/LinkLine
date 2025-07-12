package com.gustavoventieri.framework.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;

@Configuration
public class RedisConfig {
   
    private final String redisUrl;

    public RedisConfig(@Value("${spring.redis.url}") String redisUrl){
        this.redisUrl = redisUrl;
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        return RedisClient.create(redisUrl);
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<byte[], byte[]> statefulRedisConnection(RedisClient redisClient) {
        return redisClient.connect(ByteArrayCodec.INSTANCE);
    }
}
