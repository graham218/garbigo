package com.thepeacemakers.model.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Set;

import com.thepeacemakers.model.User;

@Data
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private Set<User.Role> roles;
    private User.AccountStatus accountStatus;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePictureUrl;
    private String fullName;
    private Long loginCount;
    private Instant lastLoginAt;
    private Double averageRating;
    private Long totalRatings;
    private Boolean isActive;
    private Boolean isArchived;
    private Instant createdAt;
    private Instant updatedAt;
    
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .accountStatus(user.getAccountStatus())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneVerified(user.getIsPhoneVerified())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .fullName(user.getFullName())
                .loginCount(user.getLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .averageRating(user.getAverageRating())
                .totalRatings(user.getTotalRatings())
                .isActive(user.getIsActive())
                .isArchived(user.getIsArchived())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}