package com.thepeacemakers.service;

import com.garbigo.userservice.model.User;
import com.garbigo.userservice.model.request.UpdateProfileRequest;
import com.garbigo.userservice.model.response.UserResponse;
import com.garbigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    
    public UserResponse getCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if new email is taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(request.getEmail().toLowerCase());
            user.setIsEmailVerified(false);
        }
        
        // Check if new username is taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username is already taken");
            }
            user.setUsername(request.getUsername().toLowerCase());
        }
        
        // Check if new phone number is taken
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number is already in use");
            }
            user.setPhoneNumber(request.getPhoneNumber());
            user.setIsPhoneVerified(false);
        }
        
        // Update other fields
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getMiddleName()).ifPresent(user::setMiddleName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);
        
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse updateProfilePicture(String imageUrl) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete old profile picture from Cloudinary if exists
        if (user.getProfilePictureUrl() != null) {
            cloudinaryService.deleteImage(user.getProfilePictureUrl());
        }
        
        user.setProfilePictureUrl(imageUrl);
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);
        
        return UserResponse.fromUser(user);
    }
    
    public void deleteProfilePicture() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getProfilePictureUrl() != null) {
            cloudinaryService.deleteImage(user.getProfilePictureUrl());
            user.setProfilePictureUrl(null);
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }
    }
    
    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}