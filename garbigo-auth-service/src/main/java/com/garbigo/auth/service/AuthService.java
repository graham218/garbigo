package com.garbigo.auth.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.garbigo.auth.dto.*;
import com.garbigo.auth.exception.CustomException;
import com.garbigo.auth.feign.OtherServiceClient;
import com.garbigo.auth.model.Role;
import com.garbigo.auth.model.Token;
import com.garbigo.auth.model.User;
import com.garbigo.auth.repository.TokenRepository;
import com.garbigo.auth.repository.UserRepository;
import com.garbigo.auth.security.JwtUtil;
import com.garbigo.auth.util.RateLimiter;

import jakarta.mail.MessagingException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final Cloudinary cloudinary;
    private final OtherServiceClient otherServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final RateLimiter rateLimiter;

    @Value("${rabbitmq.queue.user-created}")
    private String userCreatedQueue;

    @Value("${app.url}")
    private String appUrl;

    public AuthService(UserRepository userRepository,
                       TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       EmailService emailService,
                       Cloudinary cloudinary,
                       OtherServiceClient otherServiceClient,
                       RabbitTemplate rabbitTemplate,
                       RateLimiter rateLimiter) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.cloudinary = cloudinary;
        this.otherServiceClient = otherServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.rateLimiter = rateLimiter;
    }

    public AuthResponse signup(SignupRequest request) {
        rateLimiter.checkRateLimit();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setHomeAddress(request.getHomeAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.CLIENT);

        userRepository.save(user);

        String verifyToken = UUID.randomUUID().toString();
        Token token = new Token();
        token.setUserId(user.getId());
        token.setToken(verifyToken);
        token.setType("VERIFICATION");
        token.setExpiry(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24 hours
        tokenRepository.save(token);

        try {
			emailService.sendVerificationEmail(user.getEmail(), verifyToken);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        rabbitTemplate.convertAndSend(userCreatedQueue, user);

        return buildAuthResponse(user);
    }

    public AuthResponse signin(AuthRequest request) {
        rateLimiter.checkRateLimit();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();

        if (!user.isVerified()) {
            throw new CustomException("Account not verified");
        }

        return buildAuthResponse(user);
    }

    public void verifyAccount(String tokenStr) {
        Token token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new CustomException("Invalid verification token"));

        if (token.getExpiry() < System.currentTimeMillis()) {
            throw new CustomException("Verification token expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new CustomException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.delete(token);
    }

    public void requestPasswordReset(String email) {
        rateLimiter.checkRateLimit();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        Token token = new Token();
        token.setUserId(user.getId());
        token.setToken(resetToken);
        token.setType("RESET");
        token.setExpiry(System.currentTimeMillis() + 60 * 60 * 1000); // 1 hour
        tokenRepository.save(token);

        try {
			emailService.sendResetPasswordEmail(email, resetToken);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void resetPassword(String tokenStr, String newPassword) {
        Token token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new CustomException("Invalid reset token"));

        if (token.getExpiry() < System.currentTimeMillis()) {
            throw new CustomException("Reset token expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new CustomException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("No authenticated user");
        }
        return (User) authentication.getPrincipal();
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        response.setToken(jwtUtil.generateToken(user));
        response.setRole(user.getRole().name());
        try {
            response.setDashboardUrl(otherServiceClient.getDashboardUrl(user.getRole().name()));
        } catch (Exception e) {
            response.setDashboardUrl("/dashboard/default"); // fallback
        }
        return response;
    }

    public String uploadProfilePicture(org.springframework.web.multipart.MultipartFile file) throws IOException {
        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }
}