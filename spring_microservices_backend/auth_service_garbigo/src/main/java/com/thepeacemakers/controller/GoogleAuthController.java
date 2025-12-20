package com.thepeacemakers.controller;

import com.thepeacemakers.model.request.GoogleAuthRequest;
import com.thepeacemakers.model.response.ApiResponse;
import com.thepeacemakers.service.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {
    
    private final GoogleAuthService googleAuthService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        String token = googleAuthService.authenticateWithGoogle(request.getToken());
        return ResponseEntity.ok(ApiResponse.success("Google authentication successful", token));
    }
}