package com.thepeacemakers.service;

import com.garbigo.userservice.model.User;
import com.garbigo.userservice.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${google.client-id}")
    private String googleClientId;
    
    public String authenticateWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), 
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                
                String googleId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                
                // Find or create user
                User user = userRepository.findByGoogleId(googleId)
                        .orElseGet(() -> userRepository.findByEmail(email)
                                .orElseGet(() -> createUserFromGoogle(payload, googleId)));
                
                // Update user info if needed
                if (user.getProfilePictureUrl() == null && pictureUrl != null) {
                    user.setProfilePictureUrl(pictureUrl);
                }
                
                user.setLastLoginAt(Instant.now());
                user.setLoginCount(user.getLoginCount() != null ? user.getLoginCount() + 1 : 1);
                userRepository.save(user);
                
                // Generate JWT token
                return jwtTokenProvider.generateToken(user.getEmail());
                
            } else {
                throw new RuntimeException("Invalid Google ID token");
            }
        } catch (Exception e) {
            log.error("Google authentication failed", e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
    
    private User createUserFromGoogle(GoogleIdToken.Payload payload, String googleId) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"User", ""};
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        User user = User.builder()
                .googleId(googleId)
                .email(email.toLowerCase())
                .username(generateUsername(email))
                .firstName(firstName)
                .lastName(lastName)
                .profilePictureUrl(pictureUrl)
                .password(passwordEncoder.encode(generateRandomPassword()))
                .role(User.Role.CLIENT) // Default role for Google signups
                .accountStatus(User.AccountStatus.ACTIVE)
                .isEmailVerified(true)
                .isPhoneVerified(false)
                .twoFactorEnabled(false)
                .loginCount(1L)
                .failedLoginCount(0L)
                .averageRating(0.0)
                .totalRatings(0L)
                .archived(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        return userRepository.save(user);
    }
    
    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int suffix = 1;
        
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }
        
        return username;
    }
    
    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString();
    }
}