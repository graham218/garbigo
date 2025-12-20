package com.thepeacemakers.model.response;

import com.thepeacemakers.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private User.Role role;
        private User.AccountStatus accountStatus;
        private Boolean isEmailVerified;
        private String profilePictureUrl;
        
        public static UserResponse fromUser(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .accountStatus(user.getAccountStatus())
                    .isEmailVerified(user.getIsEmailVerified())
                    .profilePictureUrl(user.getProfilePictureUrl())
                    .build();
        }
    }
}