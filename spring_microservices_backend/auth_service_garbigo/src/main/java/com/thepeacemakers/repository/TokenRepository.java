package com.thepeacemakers.repository;

import com.thepeacemakers.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    
    Optional<Token> findByTokenAndTokenType(String token, Token.TokenType tokenType);
    
    Optional<Token> findByToken(String token);
    
    void deleteByUserIdAndTokenType(String userId, Token.TokenType tokenType);
    
    void deleteByUserId(String userId);
}