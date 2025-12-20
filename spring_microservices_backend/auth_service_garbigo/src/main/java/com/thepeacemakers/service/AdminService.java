package com.thepeacemakers.service;

import com.garbigo.userservice.model.User;
import com.garbigo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> searchUsers(String searchTerm, Boolean archived, Pageable pageable) {
        if (archived != null) {
            return userRepository.searchUsersByArchiveStatus(searchTerm, archived, pageable);
        }
        return userRepository.searchUsers(searchTerm, pageable);
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    @Transactional
    public User createUser(User user) {
        // Validate unique fields
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
        }
        
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);
        user.setTwoFactorEnabled(false);
        user.setLoginCount(0L);
        user.setFailedLoginCount(0L);
        user.setAverageRating(0.0);
        user.setTotalRatings(0L);
        user.setArchived(false);
        
        if (user.getPermissions() == null) {
            user.setPermissions(new HashSet<>());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(String id, User updatedUser) {
        User user = getUserById(id);
        
        // Check if new email is taken
        if (!updatedUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        // Check if new username is taken
        if (!updatedUser.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        
        // Check if new phone number is taken
        if (updatedUser.getPhoneNumber() != null && 
            !updatedUser.getPhoneNumber().equals(user.getPhoneNumber()) && 
            userRepository.existsByPhoneNumber(updatedUser.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
        }
        
        // Update fields
        user.setFirstName(updatedUser.getFirstName());
        user.setMiddleName(updatedUser.getMiddleName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail().toLowerCase());
        user.setUsername(updatedUser.getUsername().toLowerCase());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setRole(updatedUser.getRole());
        user.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        user.setPermissions(updatedUser.getPermissions());
        user.setUpdatedAt(Instant.now());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    @Transactional
    public User archiveUser(String id, String archivedBy) {
        User user = getUserById(id);
        user.setArchived(true);
        user.setArchivedAt(Instant.now());
        user.setArchivedBy(archivedBy);
        return userRepository.save(user);
    }
    
    @Transactional
    public User unarchiveUser(String id) {
        User user = getUserById(id);
        user.setArchived(false);
        user.setArchivedAt(null);
        user.setArchivedBy(null);
        return userRepository.save(user);
    }
    
    @Transactional
    public User activateUser(String id) {
        User user = getUserById(id);
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User deactivateUser(String id) {
        User user = getUserById(id);
        user.setAccountStatus(User.AccountStatus.INACTIVE);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User suspendUser(String id) {
        User user = getUserById(id);
        user.setAccountStatus(User.AccountStatus.SUSPENDED);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User blockUser(String id) {
        User user = getUserById(id);
        user.setAccountStatus(User.AccountStatus.BLOCKED);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User verifyEmail(String id) {
        User user = getUserById(id);
        user.setIsEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User unverifyEmail(String id) {
        User user = getUserById(id);
        user.setIsEmailVerified(false);
        user.setEmailVerifiedAt(null);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User verifyPhone(String id) {
        User user = getUserById(id);
        user.setIsPhoneVerified(true);
        user.setPhoneVerifiedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public User unverifyPhone(String id) {
        User user = getUserById(id);
        user.setIsPhoneVerified(false);
        user.setPhoneVerifiedAt(null);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getUsersByStatus(User.AccountStatus status) {
        return userRepository.findByAccountStatus(status);
    }
    
    public Page<User> getArchivedUsers(Pageable pageable) {
        return userRepository.findByArchived(true, pageable);
    }
    
    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findByArchived(false, pageable);
    }
}