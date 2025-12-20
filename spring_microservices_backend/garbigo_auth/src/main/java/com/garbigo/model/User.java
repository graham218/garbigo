package com.garbigo.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String phoneNumber;

    private String password; // hashed

    private Role role;

    private AccountStatus accountStatus = AccountStatus.PENDING;

    private Boolean isEmailVerified = false;

    private Boolean isPhoneVerified = false;

    private Boolean twoFactorEnabled = false;

    private Instant passwordChangedAt;

    private String firstName;
    private String middleName;
    private String lastName;

    private String profilePictureUrl;

    private Long loginCount = 0L;
    private Long failedLoginCount = 0L;
    private Instant lastLoginAt;

    private Double averageRating = 0.0;
    private Long totalRatings = 0L;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Role {
        CLIENT,
        COLLECTOR,
        ADMIN,
        OPERATIONS,
        FINANCE,
        SUPPORT
    }

    public enum AccountStatus {
        ACTIVE,
        PENDING,
        SUSPENDED,
        BLOCKED,
        ARCHIVED
    }
}