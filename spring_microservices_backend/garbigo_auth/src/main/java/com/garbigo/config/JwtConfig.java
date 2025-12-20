package com.garbigo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    // 30 days in milliseconds = 30 * 24 * 60 * 60 * 1000
    private final long expiration = 30L * 24 * 60 * 60 * 1000;

    public String getSecret() {
        return secret;
    }

    public long getExpiration() {
        return expiration;
    }
}