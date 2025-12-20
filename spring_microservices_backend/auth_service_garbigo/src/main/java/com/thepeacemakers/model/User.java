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

    // Authentication & Security
    private String password;
    
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Builder.Default
    private Boolean isEmailVerified = false;
    
    @Builder.Default
    private Boolean isPhoneVerified = false;
    
    @Builder.Default
    private Boolean twoFactorEnabled = false;

    private Instant passwordChangedAt;

    // Personal Info
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePictureUrl;

    // Google OAuth
    private String provider;
    private String providerId;

    // Login & Usage Stats
    @Builder.Default
    private Long loginCount = 0L;
    
    @Builder.Default
    private Long failedLoginCount = 0L;
    
    private Instant lastLoginAt;
    
    @Builder.Default
    private Instant lastFailedLoginAt = null;

    // Ratings
    @Builder.Default
    private Double averageRating = 0.0;
    
    @Builder.Default
    private Long totalRatings = 0L;

    // Metadata
    @Builder.Default
    private Boolean isArchived = false;
    
    @Builder.Default
    private Boolean isActive = true;

    // Audit Fields
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // Enums
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
        DEACTIVATED
    }

    // Helper methods
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }
}