package com.NTG.mirathy.controller;

import com.NTG.mirathy.DTOs.response.AuthResponse;
import com.NTG.mirathy.DTOs.request.LoginRequest;
import com.NTG.mirathy.DTOs.request.SignupRequest;
import com.NTG.mirathy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


//    @GetMapping("/me")
//    public ResponseEntity<AuthResponse> getCurrentUser() {
//        var user = authService.getCurrentUser();
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        AuthResponse response = AuthResponse.builder()
//                .userId(user.getId())
//                .fullName(user.getFullName())
//                .email(user.getEmail())
//                .role(user.getRole())
//                .isActive(user.getIsActive())
//                .createdAt(user.getCreatedAt())
//                .message("User details retrieved successfully")
//                .success(true)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
}
