package com.garbigo.dto;

public record AuthResponse(
        String token,
        String userId,
        String email,
        String role,
        String firstName,
        String lastName,
        String profilePictureUrl
) {}