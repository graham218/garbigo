package com.thepeacemakers.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    private String password;
    
    private Role role;

    private AccountStatus accountStatus;

    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean twoFactorEnabled;

    private Instant passwordChangedAt;
    private Instant emailVerifiedAt;
    private Instant phoneVerifiedAt;

    private String firstName;
    private String middleName;
    private String lastName;

    private String profilePictureUrl;
    private String googleId;
    private String googleProfilePicture;

    private Long loginCount;
    private Long failedLoginCount;
    private Instant lastLoginAt;
    private Instant lastFailedLoginAt;

    private Double averageRating;
    private Long totalRatings;

    private Set<String> permissions = new HashSet<>();

    private Boolean archived;
    private Instant archivedAt;
    private String archivedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Role {
        ADMIN,
        CLIENT,
        COLLECTOR,
        OPERATIONS,
        FINANCE,
        SUPPORT
    }

    public enum AccountStatus {
        ACTIVE,
        PENDING,
        SUSPENDED,
        BLOCKED,
        INACTIVE
    }
}