package com.garbigo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "verification_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "token")  // Don't log the full token
@EqualsAndHashCode(of = "id")
public class VerificationToken {

    @Id
    private String id;

    private String token;

    private String userId;

    private Instant expiryDate;
}