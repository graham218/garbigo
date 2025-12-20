package com.thepeacemakers.controller;

import com.thepeacemakers.model.request.UpdateProfileRequest;
import com.thepeacemakers.model.response.ApiResponse;
import com.thepeacemakers.model.response.UserResponse;
import com.thepeacemakers.service.CloudinaryService;
import com.thepeacemakers.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }
    
    @PostMapping("/profile/picture")
    public ResponseEntity<ApiResponse> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file);
        UserResponse user = userService.updateProfilePicture(imageUrl);
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", user));
    }
    
    @DeleteMapping("/profile/picture")
    public ResponseEntity<ApiResponse> deleteProfilePicture() {
        userService.deleteProfilePicture();
        return ResponseEntity.ok(ApiResponse.success("Profile picture deleted successfully"));
    }
}