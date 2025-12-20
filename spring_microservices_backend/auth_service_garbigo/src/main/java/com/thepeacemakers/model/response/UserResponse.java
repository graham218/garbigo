package com.thepeacemakers.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thepeacemakers.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    
    private String id;
    private String email;
    private String username;
    private String phoneNumber;
    
    private String firstName;
    private String middleName;
    private String lastName;
    
    private User.Role role;
    private User.AccountStatus accountStatus;
    
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean twoFactorEnabled;
    private Boolean archived;
    
    private String profilePictureUrl;
    private String googleId;
    private String googleProfilePicture;
    
    private Long loginCount;
    private Long failedLoginCount;
    private Instant lastLoginAt;
    private Instant lastFailedLoginAt;
    
    private Double averageRating;
    private Long totalRatings;
    
    private Set<String> permissions;
    
    private Instant createdAt;
    private Instant updatedAt;
    private Instant passwordChangedAt;
    private Instant emailVerifiedAt;
    private Instant phoneVerifiedAt;
    private Instant archivedAt;
    private String archivedBy;
    
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneVerified(user.getIsPhoneVerified())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .archived(user.getArchived())
                .profilePictureUrl(user.getProfilePictureUrl())
                .googleId(user.getGoogleId())
                .googleProfilePicture(user.getGoogleProfilePicture())
                .loginCount(user.getLoginCount())
                .failedLoginCount(user.getFailedLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .lastFailedLoginAt(user.getLastFailedLoginAt())
                .averageRating(user.getAverageRating())
                .totalRatings(user.getTotalRatings())
                .permissions(user.getPermissions())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .passwordChangedAt(user.getPasswordChangedAt())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .phoneVerifiedAt(user.getPhoneVerifiedAt())
                .archivedAt(user.getArchivedAt())
                .archivedBy(user.getArchivedBy())
                .build();
    }
    
    // Simplified version for public APIs
    public static UserResponse publicView(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .averageRating(user.getAverageRating())
                .totalRatings(user.getTotalRatings())
                .build();
    }
    
    // Admin view with all details
    public static UserResponse adminView(User user) {
        return fromUser(user);
    }
    
    // Self view for logged-in user
    public static UserResponse selfView(User user) {
        UserResponse response = fromUser(user);
        // Remove sensitive information that shouldn't be shown to user
        response.setArchived(null);
        response.setArchivedAt(null);
        response.setArchivedBy(null);
        return response;
    }
}