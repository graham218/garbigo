package com.thepeacemakers.service;

import com.garbigo.userservice.model.Token;
import com.garbigo.userservice.model.User;
import com.garbigo.userservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final TokenRepository tokenRepository;
    
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void scheduleTokenCleanup() {
        cleanupExpiredTokens();
    }
}