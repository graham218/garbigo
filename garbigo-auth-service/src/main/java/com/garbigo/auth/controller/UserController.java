package com.garbigo.auth.controller;

import com.garbigo.auth.dto.ProfileUpdateRequest;
import com.garbigo.auth.dto.UserDto;
import com.garbigo.auth.model.User;
import com.garbigo.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(userService.getAllUsers(search));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/archive")
    public ResponseEntity<String> archiveUser(@PathVariable String id) {
        userService.archiveUser(id);
        return ResponseEntity.ok("User archived");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unarchive")
    public ResponseEntity<String> unarchiveUser(@PathVariable String id) {
        userService.unarchiveUser(id);
        return ResponseEntity.ok("User unarchived");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable String id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable String id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/verify")
    public ResponseEntity<String> verifyUser(@PathVariable String id) {
        userService.verifyUser(id);
        return ResponseEntity.ok("User verified");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unverify")
    public ResponseEntity<String> unverifyUser(@PathVariable String id) {
        userService.unverifyUser(id);
        return ResponseEntity.ok("User unverified");
    }
}