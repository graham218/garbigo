package com.thepeacemakers.controller;

import com.thepeacemakers.model.request.*;
import com.thepeacemakers.model.response.ApiResponse;
import com.thepeacemakers.model.response.AuthResponse;
import com.thepeacemakers.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignupRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. Please check your email to verify your account.", authResponse));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestParam String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        authService.logout(jwt);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset instructions sent to your email"));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String userEmail = getCurrentUserEmail();
        authService.changePassword(userEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
    
    private String getCurrentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }
}