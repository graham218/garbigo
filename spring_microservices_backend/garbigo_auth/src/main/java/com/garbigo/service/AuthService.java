package com.garbigo.service;

import com.garbigo.dto.*;
import com.garbigo.model.PasswordResetToken;
import com.garbigo.model.User;
import com.garbigo.model.User.AccountStatus;
import com.garbigo.model.User.Role;
import com.garbigo.model.VerificationToken;
import com.garbigo.repository.UserRepository;
import com.garbigo.security.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    /**
     * User signup (client or collector)
     */
    public AuthResponse signup(AuthRequest request, Role role) throws MessagingException {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .accountStatus(AccountStatus.PENDING)
                .isEmailVerified(false)
                .createdAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);

        // Send verification email
        VerificationToken verificationToken = tokenService.createVerificationToken(savedUser);
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());

        // Generate long-lived JWT (30 days)
        String jwt = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        savedUser.getEmail(),
                        savedUser.getPassword(),
                        savedUser.getAuthorities()
                )
        );

        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getProfilePictureUrl()
        );
    }

    /**
     * User login
     */
    public AuthResponse signin(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (!Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new RuntimeException("Email not verified");
        }

        // Update login stats
        user.setLastLoginAt(Instant.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);

        // Generate 30-day JWT
        String jwt = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.getAuthorities()
                )
        );

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureUrl()
        );
    }

    /**
     * Verify email from link
     */
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenService.getVerificationToken(token);
        if (verificationToken == null || tokenService.isTokenExpired(verificationToken.getExpiryDate())) {
            throw new RuntimeException("Invalid or expired verification token");
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        tokenService.deleteVerificationToken(verificationToken);
    }

    /**
     * Send password reset email
     */
    public void forgotPassword(PasswordResetRequest request) throws MessagingException {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken resetToken = tokenService.createPasswordResetToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    /**
     * Reset password using token from email
     */
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenService.getPasswordResetToken(token);
        if (resetToken == null || tokenService.isTokenExpired(resetToken.getExpiryDate())) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        tokenService.deletePasswordResetToken(resetToken);
    }

    /**
     * Change password (requires old password)
     */
    public void changePassword(User user, PasswordChangeRequest request) {
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
    }
}