package com.thepeacemakers.service;

import com.thepeacemakers.model.User;
import com.thepeacemakers.model.request.AdminUpdateUserRequest;
import com.thepeacemakers.model.response.UserResponse;
import com.thepeacemakers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageable);
        
        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        
        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
    }
    
    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getUsersByRole(User.Role role) {
        List<User> users = userRepository.findByRolesContaining(role);
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getUsersByStatus(User.AccountStatus status) {
        List<User> users = userRepository.findByAccountStatus(status);
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse updateUser(String userId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(userId)
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
        
        if (request.getPhoneNumber() != null && 
            !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already registered");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }
        
        if (request.getAccountStatus() != null) {
            user.setAccountStatus(request.getAccountStatus());
        }
        
        if (request.getIsEmailVerified() != null) {
            user.setIsEmailVerified(request.getIsEmailVerified());
        }
        
        if (request.getIsPhoneVerified() != null) {
            user.setIsPhoneVerified(request.getIsPhoneVerified());
        }
        
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
            if (!request.getIsActive()) {
                user.setAccountStatus(User.AccountStatus.DEACTIVATED);
            }
        }
        
        if (request.getIsArchived() != null) {
            user.setIsArchived(request.getIsArchived());
        }
        
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
    
    @Transactional
    public UserResponse archiveUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsArchived(true);
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse unarchiveUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsArchived(false);
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse activateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        User updatedUser = userRepository.save(user);
        
        // Send activation email
        emailService.sendAccountActivatedEmail(user.getEmail());
        
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        user.setAccountStatus(User.AccountStatus.DEACTIVATED);
        User updatedUser = userRepository.save(user);
        
        // Send deactivation email
        emailService.sendAccountDeactivatedEmail(user.getEmail());
        
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse verifyEmail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEmailVerified(true);
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse unverifyEmail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEmailVerified(false);
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse suspendUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(User.AccountStatus.SUSPENDED);
        User updatedUser = userRepository.save(user);
        
        // Send suspension email
        emailService.sendAccountSuspendedEmail(user.getEmail());
        
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse blockUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(User.AccountStatus.BLOCKED);
        User updatedUser = userRepository.save(user);
        
        // Send block email
        emailService.sendAccountBlockedEmail(user.getEmail());
        
        return UserResponse.fromUser(updatedUser);
    }
    
    @Transactional
    public UserResponse createUser(User user) {
        // Check if user exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        
        User savedUser = userRepository.save(user);
        
        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        
        return UserResponse.fromUser(savedUser);
    }
}