package com.garbigo.dto;

import com.garbigo.model.User;

public record UserDto(
        String id,
        String username,
        String email,
        String phoneNumber,
        String role,
        String accountStatus,
        Boolean isEmailVerified,
        String firstName,
        String middleName,
        String lastName,
        String profilePictureUrl
) {
    public static UserDto fromUser(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getAccountStatus().name(),
                user.getIsEmailVerified(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getProfilePictureUrl()
        );
    }
}