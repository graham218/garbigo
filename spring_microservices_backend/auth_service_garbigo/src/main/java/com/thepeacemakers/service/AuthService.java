package com.thepeacemakers.service;

import com.thepeacemakers.model.*;
import com.thepeacemakers.model.request.*;
import com.thepeacemakers.model.response.AuthResponse;
import com.thepeacemakers.model.response.UserResponse;
import com.thepeacemakers.repository.UserRepository;
import com.thepeacemakers.repository.TokenRepository;
import com.thepeacemakers.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOGIN_ATTEMPTS_KEY = "login_attempts:";
    
    @Transactional
    public User register(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Check if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        
        if (request.getPhoneNumber() != null && 
            userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .roles(new HashSet<>())
                .build();
        
        // Add role
        user.addRole(request.getRole());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Send verification email
        sendVerificationEmail(savedUser);
        
        return savedUser;
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Check login attempts
        String loginKey = LOGIN_ATTEMPTS_KEY + request.getEmailOrUsername();
        Integer attempts = (Integer) redisTemplate.opsForValue().get(loginKey);
        
        if (attempts != null && attempts >= 5) {
            throw new RuntimeException("Account locked due to too many failed attempts. Try again in 15 minutes");
        }
        
        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmailOrUsername(),
                    request.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user
            User user = userRepository.findByEmailOrUsername(
                request.getEmailOrUsername(), 
                request.getEmailOrUsername()
            ).orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if account is active
            if (!user.getIsActive()) {
                throw new RuntimeException("Account is deactivated");
            }
            
            if (user.getAccountStatus() == User.AccountStatus.BLOCKED || 
                user.getAccountStatus() == User.AccountStatus.SUSPENDED) {
                throw new RuntimeException("Account is " + user.getAccountStatus().toString().toLowerCase());
            }
            
            // Update login stats
            user.setLoginCount(user.getLoginCount() + 1);
            user.setLastLoginAt(Instant.now());
            user.setFailedLoginCount(0L);
            userRepository.save(user);
            
            // Clear login attempts
            redisTemplate.delete(loginKey);
            
            // Generate tokens
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // Cache token
            cacheUserToken(user.getId(), token);
            
            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L) // 1 hour
                    .user(UserResponse.fromUser(user))
                    .build();
                    
        } catch (Exception e) {
            // Increment failed attempts
            redisTemplate.opsForValue().increment(loginKey, 1);
            redisTemplate.expire(loginKey, 15, TimeUnit.MINUTES);
            
            // Update user failed login count
            userRepository.findByEmailOrUsername(
                request.getEmailOrUsername(), 
                request.getEmailOrUsername()
            ).ifPresent(user -> {
                user.setFailedLoginCount(user.getFailedLoginCount() + 1);
                user.setLastFailedLoginAt(Instant.now());
                userRepository.save(user);
            });
            
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @Transactional
    public void sendVerificationEmail(User user) {
        // Delete existing verification tokens
        tokenRepository.deleteByUserIdAndTokenType(
            user.getId(), 
            Token.TokenType.EMAIL_VERIFICATION
        );
        
        // Create verification token
        String token = UUID.randomUUID().toString();
        Token verificationToken = Token.builder()
                .token(token)
                .user(user)
                .tokenType(Token.TokenType.EMAIL_VERIFICATION)
                .expiryDate(Instant.now().plusSeconds(24 * 60 * 60)) // 24 hours
                .used(false)
                .createdAt(Instant.now())
                .build();
        
        tokenRepository.save(verificationToken);
        
        // Send email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    @Transactional
    public void verifyEmail(String token) {
        Token verificationToken = tokenRepository
                .findByTokenAndTokenType(token, Token.TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        if (!verificationToken.isValid()) {
            throw new RuntimeException("Token expired or already used");
        }
        
        User user = verificationToken.getUser();
        user.setIsEmailVerified(true);
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);
        
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Delete existing reset tokens
        tokenRepository.deleteByUserIdAndTokenType(
            user.getId(), 
            Token.TokenType.PASSWORD_RESET
        );
        
        // Create reset token
        String token = UUID.randomUUID().toString();
        Token resetToken = Token.builder()
                .token(token)
                .user(user)
                .tokenType(Token.TokenType.PASSWORD_RESET)
                .expiryDate(Instant.now().plusSeconds(2 * 60 * 60)) // 2 hours
                .used(false)
                .createdAt(Instant.now())
                .build();
        
        tokenRepository.save(resetToken);
        
        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        Token resetToken = tokenRepository
                .findByTokenAndTokenType(request.getToken(), Token.TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (!resetToken.isValid()) {
            throw new RuntimeException("Token expired or already used");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        // Invalidate all user sessions
        invalidateUserSessions(user.getId());
    }
    
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
        
        // Invalidate all user sessions
        invalidateUserSessions(userId);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, jwtTokenProvider.getAuthorities(refreshToken)
            );
            
            String newToken = jwtTokenProvider.generateToken(authentication);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            cacheUserToken(user.getId(), newToken);
            
            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .user(UserResponse.fromUser(user))
                    .build();
        }
        
        throw new RuntimeException("Invalid refresh token");
    }
    
    @Transactional
    public void logout(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Remove token from cache
        redisTemplate.delete("user_token:" + user.getId());
    }
    
    private void cacheUserToken(String userId, String token) {
        String key = "user_token:" + userId;
        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.HOURS);
    }
    
    private void invalidateUserSessions(String userId) {
        redisTemplate.delete("user_token:" + userId);
    }
}