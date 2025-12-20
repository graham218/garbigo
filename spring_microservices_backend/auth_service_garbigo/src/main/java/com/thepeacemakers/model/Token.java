package com.thepeacemakers.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.Instant;

@Document(collection = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @DBRef
    private User user;
    
    private TokenType tokenType;
    
    private Instant expiryDate;
    
    private Boolean used;
    
    private Instant createdAt;
    
    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }
    
    public boolean isValid() {
        return !used && expiryDate.isAfter(Instant.now());
    }
}