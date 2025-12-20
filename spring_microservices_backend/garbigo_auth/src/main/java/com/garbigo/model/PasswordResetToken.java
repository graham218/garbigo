package com.garbigo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "password_reset_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "token")
@EqualsAndHashCode(of = "id")
public class PasswordResetToken {

    @Id
    private String id;

    private String token;

    private String userId;

    private Instant expiryDate;
}