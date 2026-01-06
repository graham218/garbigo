package com.garbigo.auth.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConfigurationBuilder;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.RedissonProxyManager;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Value("${rate-limit.requests-per-minute}")
    private int requestsPerMinute;

    @Bean
    public ProxyManager<String> proxyManager(RedissonClient redissonClient) {
        return new RedissonProxyManager<>(redissonClient);
    }

    @Bean
    public BucketConfiguration bucketConfiguration() {
        return ConfigurationBuilder.newBuilder()
                .addLimit(Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1)))
                .build();
    }
}