package com.thepeacemakers.service;

import com.thepeacemakers.model.User;
import com.thepeacemakers.model.request.UpdateProfileRequest;
import com.thepeacemakers.model.response.UserResponse;
import com.thepeacemakers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    
    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        
        if (request.getMiddleName() != null) {
            user.setMiddleName(request.getMiddleName());
        }
        
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        
        if (request.getPhoneNumber() != null) {
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) && 
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already registered");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getProfilePictureUrl() != null) {
            // Delete old profile picture if exists
            if (user.getProfilePictureUrl() != null) {
                cloudinaryService.deleteImage(user.getProfilePictureUrl());
            }
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromUser(user);
    }
    
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromUser(user);
    }
}