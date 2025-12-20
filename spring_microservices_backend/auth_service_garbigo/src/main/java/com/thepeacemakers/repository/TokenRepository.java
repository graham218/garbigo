package com.thepeacemakers.repository;

import com.thepeacemakers.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    
    Optional<Token> findByToken(String token);
    
    Optional<Token> findByTokenAndTokenType(String token, Token.TokenType tokenType);
    
    Optional<Token> findByUserIdAndTokenType(String userId, Token.TokenType tokenType);
    
    void deleteByUserId(String userId);
    
    void deleteByExpiresAtBefore(java.time.Instant expiryDate);
}