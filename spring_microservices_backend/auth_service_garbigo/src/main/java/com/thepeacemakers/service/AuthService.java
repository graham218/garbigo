package com.thepeacemakers.service;

import com.garbigo.userservice.model.*;
import com.garbigo.userservice.model.request.*;
import com.garbigo.userservice.model.response.AuthResponse;
import com.garbigo.userservice.repository.RedisRepository;
import com.garbigo.userservice.repository.TokenRepository;
import com.garbigo.userservice.repository.UserRepository;
import com.garbigo.userservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    
    @Value("${app.max-login-attempts}")
    private int maxLoginAttempts;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Transactional
    public AuthResponse register(SignupRequest request) {
        // Validate unique fields
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
        }
        
        // Validate role
        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role specified");
        }
        
        // Create user
        User user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .accountStatus(User.AccountStatus.PENDING)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .twoFactorEnabled(false)
                .loginCount(0L)
                .failedLoginCount(0L)
                .averageRating(0.0)
                .totalRatings(0L)
                .archived(false)
                .permissions(new java.util.HashSet<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        user = userRepository.save(user);
        
        // Create email verification token
        String verificationToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token(verificationToken)
                .user(user)
                .tokenType(Token.TokenType.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plusSeconds(24 * 3600)) // 24 hours
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        tokenRepository.save(token);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        
        // Generate JWT token for immediate login
        String jwtToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getJwtExpirationInMs())
                .user(AuthResponse.UserResponse.fromUser(user))
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        // Check login attempts
        Integer attempts = redisRepository.getLoginAttempts(request.getEmailOrUsername());
        if (attempts >= maxLoginAttempts) {
            throw new RuntimeException("Account is locked due to too many failed attempts. Please try again later or reset your password.");
        }
        
        User user = userRepository.findByEmail(request.getEmailOrUsername().toLowerCase())
                .orElseGet(() -> userRepository.findByUsername(request.getEmailOrUsername().toLowerCase())
                        .orElseThrow(() -> {
                            redisRepository.saveLoginAttempt(request.getEmailOrUsername(), attempts + 1);
                            return new BadCredentialsException("Invalid credentials");
                        }));
        
        // Check account status
        if (user.getAccountStatus() == User.AccountStatus.BLOCKED) {
            throw new RuntimeException("Your account has been blocked. Please contact support.");
        }
        
        if (user.getAccountStatus() == User.AccountStatus.SUSPENDED) {
            throw new RuntimeException("Your account has been suspended. Please contact support.");
        }
        
        if (user.getAccountStatus() == User.AccountStatus.INACTIVE) {
            throw new RuntimeException("Your account is inactive. Please contact support to reactivate.");
        }
        
        // Authenticate
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Clear failed login attempts
            redisRepository.clearLoginAttempts(request.getEmailOrUsername());
            
            // Update user login stats
            user.setLoginCount(user.getLoginCount() != null ? user.getLoginCount() + 1 : 1);
            user.setLastLoginAt(Instant.now());
            user.setFailedLoginCount(0L);
            userRepository.save(user);
            
            // Generate tokens
            String jwtToken = jwtTokenProvider.generateToken(user.getEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtTokenProvider.getJwtExpirationInMs())
                    .user(AuthResponse.UserResponse.fromUser(user))
                    .build();
            
        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            user.setFailedLoginCount(user.getFailedLoginCount() != null ? user.getFailedLoginCount() + 1 : 1);
            user.setLastFailedLoginAt(Instant.now());
            userRepository.save(user);
            
            redisRepository.saveLoginAttempt(request.getEmailOrUsername(), attempts + 1);
            
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @Transactional
    public void verifyEmail(String token) {
        Token verificationToken = tokenRepository.findByTokenAndTokenType(token, Token.TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        if (verificationToken.isExpired()) {
            throw new RuntimeException("Verification token has expired");
        }
        
        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }
        
        User user = verificationToken.getUser();
        user.setIsEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);
        
        verificationToken.setUsedAt(Instant.now());
        tokenRepository.save(verificationToken);
    }
    
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Invalidate any existing password reset tokens
        tokenRepository.findByUserIdAndTokenType(user.getId(), Token.TokenType.PASSWORD_RESET)
                .ifPresent(tokenRepository::delete);
        
        // Create new password reset token
        String resetToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token(resetToken)
                .user(user)
                .tokenType(Token.TokenType.PASSWORD_RESET)
                .expiresAt(Instant.now().plusSeconds(30 * 60)) // 30 minutes
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        tokenRepository.save(token);
        
        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        Token resetToken = tokenRepository.findByTokenAndTokenType(request.getToken(), Token.TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }
        
        if (resetToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        
        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);
        
        // Clear any JWT tokens for this user (force logout from all devices)
        // This would require a token blacklisting mechanism
    }
    
    @Transactional
    public void changePassword(String userEmail, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Check if new password is same as old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as current password");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        
        // Optionally, invalidate all existing tokens for this user
        // tokenRepository.deleteByUserId(user.getId());
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String newToken = jwtTokenProvider.generateToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);
        
        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getJwtExpirationInMs())
                .user(AuthResponse.UserResponse.fromUser(user))
                .build();
    }
    
    public void logout(String token) {
        // Add token to blacklist
        long expiry = jwtTokenProvider.getExpirationFromToken(token).getTime() - System.currentTimeMillis();
        if (expiry > 0) {
            redisRepository.saveJwtBlacklist(token, expiry);
        }
    }
}