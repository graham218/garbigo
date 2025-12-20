package com.thepeacemakers.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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

    @DocumentReference
    private User user;

    private TokenType tokenType;

    private Instant expiresAt;
    private Instant usedAt;

    private Instant createdAt;
    private Instant updatedAt;

    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET,
        ACCOUNT_UNLOCK,
        TWO_FACTOR
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }
}