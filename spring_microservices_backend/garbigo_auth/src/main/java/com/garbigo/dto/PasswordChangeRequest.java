package com.garbigo.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword,
        @NotBlank String confirmPassword
) {}