package com.thepeacemakers.controller;

import com.thepeacemakers.model.User;
import com.thepeacemakers.model.response.ApiResponse;
import com.thepeacemakers.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse> searchUsers(
            @RequestParam String query,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminService.searchUsers(query, archived, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", users));
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String id) {
        User user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        User createdUser = adminService.createUser(user);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", createdUser));
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String id, @RequestBody User user) {
        User updatedUser = adminService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
    
    @PostMapping("/users/{id}/archive")
    public ResponseEntity<ApiResponse> archiveUser(@PathVariable String id, @RequestParam String archivedBy) {
        User user = adminService.archiveUser(id, archivedBy);
        return ResponseEntity.ok(ApiResponse.success("User archived successfully", user));
    }
    
    @PostMapping("/users/{id}/unarchive")
    public ResponseEntity<ApiResponse> unarchiveUser(@PathVariable String id) {
        User user = adminService.unarchiveUser(id);
        return ResponseEntity.ok(ApiResponse.success("User unarchived successfully", user));
    }
    
    @PostMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable String id) {
        User user = adminService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", user));
    }
    
    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable String id) {
        User user = adminService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", user));
    }
    
    @PostMapping("/users/{id}/suspend")
    public ResponseEntity<ApiResponse> suspendUser(@PathVariable String id) {
        User user = adminService.suspendUser(id);
        return ResponseEntity.ok(ApiResponse.success("User suspended successfully", user));
    }
    
    @PostMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse> blockUser(@PathVariable String id) {
        User user = adminService.blockUser(id);
        return ResponseEntity.ok(ApiResponse.success("User blocked successfully", user));
    }
    
    @PostMapping("/users/{id}/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@PathVariable String id) {
        User user = adminService.verifyEmail(id);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", user));
    }
    
    @PostMapping("/users/{id}/unverify-email")
    public ResponseEntity<ApiResponse> unverifyEmail(@PathVariable String id) {
        User user = adminService.unverifyEmail(id);
        return ResponseEntity.ok(ApiResponse.success("Email unverified successfully", user));
    }
    
    @PostMapping("/users/{id}/verify-phone")
    public ResponseEntity<ApiResponse> verifyPhone(@PathVariable String id) {
        User user = adminService.verifyPhone(id);
        return ResponseEntity.ok(ApiResponse.success("Phone verified successfully", user));
    }
    
    @PostMapping("/users/{id}/unverify-phone")
    public ResponseEntity<ApiResponse> unverifyPhone(@PathVariable String id) {
        User user = adminService.unverifyPhone(id);
        return ResponseEntity.ok(ApiResponse.success("Phone unverified successfully", user));
    }
    
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole(@PathVariable User.Role role) {
        List<User> users = adminService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/users/status/{status}")
    public ResponseEntity<ApiResponse> getUsersByStatus(@PathVariable User.AccountStatus status) {
        List<User> users = adminService.getUsersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/users/archived")
    public ResponseEntity<ApiResponse> getArchivedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminService.getArchivedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Archived users retrieved successfully", users));
    }
    
    @GetMapping("/users/active")
    public ResponseEntity<ApiResponse> getActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminService.getActiveUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", users));
    }
}