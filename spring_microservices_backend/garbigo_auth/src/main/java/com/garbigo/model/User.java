package com.garbigo.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {  // ← THIS IS THE KEY FIX

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

    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Builder.Default
    private Boolean isEmailVerified = false;

    @Builder.Default
    private Boolean isPhoneVerified = false;

    @Builder.Default
    private Boolean twoFactorEnabled = false;

    private Instant passwordChangedAt;

    private String firstName;
    private String middleName;
    private String lastName;

    private String profilePictureUrl;

    // Location fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String country = "Kenya";

    private Double latitude;
    private Double longitude;

    private Instant locationUpdatedAt;

    private Long loginCount = 0L;
    private Long failedLoginCount = 0L;
    private Instant lastLoginAt;

    private Double averageRating = 0.0;
    private Long totalRatings = 0L;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // === UserDetails Methods ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // We use email as username for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE && Boolean.TRUE.equals(isEmailVerified);
    }

    // === Enums ===
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

    // === Helper Methods ===
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null) sb.append(addressLine1).append(", ");
        if (addressLine2 != null) sb.append(addressLine2).append(", ");
        if (city != null) sb.append(city).append(", ");
        if (stateOrProvince != null) sb.append(stateOrProvince).append(", ");
        if (postalCode != null) sb.append(postalCode).append(", ");
        if (country != null) sb.append(country);
        String addr = sb.toString();
        return addr.endsWith(", ") ? addr.substring(0, addr.length() - 2) : addr.isEmpty() ? "No address provided" : addr;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }
}