package com.garbigo.controller;

import com.garbigo.dto.*;
import com.garbigo.model.User;
import com.garbigo.model.User.Role;
import com.garbigo.service.AuthService;
import com.garbigo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // Client signup
    @PostMapping("/signup/client")
    public ResponseEntity<AuthResponse> signupClient(@Valid @RequestBody AuthRequest request) throws MessagingException {
        return ResponseEntity.ok(authService.signup(request, Role.CLIENT));
    }

    // Collector signup
    @PostMapping("/signup/collector")
    public ResponseEntity<AuthResponse> signupCollector(@Valid @RequestBody AuthRequest request) throws MessagingException {
        return ResponseEntity.ok(authService.signup(request, Role.COLLECTOR));
    }

    // Login
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    // Email verification
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully! You can now log in.");
    }

    // Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody PasswordResetRequest request) throws MessagingException {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    // Reset password (from email link)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestBody PasswordResetRequest request) {
        authService.resetPassword(token, request.email()); // using email field for new password
        return ResponseEntity.ok("Password reset successfully");
    }

    // Change password (old → new)
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PasswordChangeRequest request) {
        authService.changePassword(user, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // Update Profile - FULL SUPPORT FOR LOCATION FIELDS & PHOTO
    @PostMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal User user,

            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,

            // New location fields
            @RequestParam(required = false) String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String stateOrProvince,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,

            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,

            @RequestParam(required = false) MultipartFile profilePicture) throws IOException {

        return ResponseEntity.ok(
                userService.updateProfile(
                        user,
                        firstName, middleName, lastName, phoneNumber,
                        addressLine1, addressLine2, city, stateOrProvince, postalCode, country,
                        latitude, longitude,
                        profilePicture
                )
        );
    }
}