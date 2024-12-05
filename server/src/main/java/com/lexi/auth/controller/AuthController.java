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
    public ResponseEntity<SignupResponse> signupUser(@Valid @RequestBody SignupRequest signupRequest) {
        authService.validateSignupRequest(signupRequest);
        userService.saveUser(authService.registerUser(signupRequest));
        SignupResponse response = new SignupResponse(signupRequest.getUsername(), signupRequest.getEmail(), "Signup successful!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request: {}", loginRequest);

        JwtResponse jwtResponse = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse jwtResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@RequestParam String username, @RequestParam String refreshToken) {
        try {
            authService.logout(username, refreshToken);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
