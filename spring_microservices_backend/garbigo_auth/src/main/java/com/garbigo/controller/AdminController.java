package com.garbigo.controller;

import com.garbigo.dto.UserDto;
import com.garbigo.model.User.AccountStatus;
import com.garbigo.model.User.Role;
import com.garbigo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Search users by any field (email, name, phone, city, etc.)
     * If no query provided, returns all users
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String q) {
        List<UserDto> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users (convenience endpoint)
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.searchUsers("");
        return ResponseEntity.ok(users);
    }

    /**
     * Create a new user (admin only)
     * Password is hashed automatically
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Role role,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber) {

        UserDto createdUser = userService.createUser(email, password, role, firstName, middleName, lastName, phoneNumber);
        return ResponseEntity.status(201).body(createdUser);
    }

    /**
     * Update user details (admin only)
     * Any field can be updated independently
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String id,

            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) Boolean emailVerified,
            @RequestParam(required = false) Boolean phoneVerified,

            // Location update
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {

        UserDto updatedUser = userService.adminUpdateUser(
                id, role, status, emailVerified, phoneVerified, latitude, longitude
        );
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user permanently
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.adminDeleteUser(id);
        return ResponseEntity.noContent().build();
    }
}