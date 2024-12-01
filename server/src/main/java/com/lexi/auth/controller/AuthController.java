package com.lexi.auth.controller;

import com.lexi.auth.exception.EmailAlreadyExistsException;
import com.lexi.auth.exception.TokenRefreshException;
import com.lexi.auth.exception.UsernameAlreadyExistsException;
import com.lexi.auth.model.RefreshToken;
import com.lexi.auth.service.RefreshTokenService;
import com.lexi.auth.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.lexi.auth.dto.*;
import com.lexi.auth.service.AuthService;
import com.lexi.auth.service.UserService;
import com.lexi.common.dto.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signupUser(@Valid @RequestBody SignupRequest signupRequest) {
        authService.validateSignupRequest(signupRequest);
        userService.saveUser(authService.registerUser(signupRequest));
        SignupResponse response = new SignupResponse(signupRequest.getUsername(), signupRequest.getEmail(), "Signup successful!");
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Login successful!"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse jwtResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Token refreshed successfully!"));
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser(@RequestParam String username) {
        try {
            authService.logout(username);
            return ResponseEntity.ok(ApiResponse.success(null, "Logout successful!"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

}
