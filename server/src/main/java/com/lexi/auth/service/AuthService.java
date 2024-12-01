package com.lexi.auth.service;

import com.lexi.auth.dto.SignupRequest;
import com.lexi.auth.exception.EmailAlreadyExistsException;
import com.lexi.auth.exception.TokenRefreshException;
import com.lexi.auth.exception.UsernameAlreadyExistsException;
import com.lexi.auth.model.RefreshToken;
import com.lexi.auth.service.impl.UserDetailsImpl;
import com.lexi.auth.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lexi.auth.dto.JwtResponse;
import com.lexi.auth.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles authentication and token-related logic.
 * Manages authentication, registration, token generation, and refresh token workflows.
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public JwtResponse authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        log.info("Generating JWT token for user {}", username);

        String accessToken = jwtTokenProvider.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Creating refresh token for user {}", userDetails.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public User registerUser(@NotNull SignupRequest signUpRequest) {
        validateSignupRequest(signUpRequest);

        log.info("Creating user account for {}", signUpRequest.getUsername());
        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole("ROLE_USER");

        log.info("Saving user to database");
        return userService.saveUser(user);
    }

    public void logout(String username, String token) {
        log.info("Revoking refresh token for user {}", username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        refreshTokenService.revokeToken(token);
        log.info("User logged out: {}", username);
    }


    public void validateSignupRequest(SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.getUsername())) {
            log.error("Username already exists: {}", signupRequest.getUsername());
            throw new UsernameAlreadyExistsException("Username is already taken!");
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            log.error("Email already exists: {}", signupRequest.getEmail());
            throw new EmailAlreadyExistsException("Email is already in use!");
        }
    }

    public JwtResponse refreshAccessToken(String refreshToken) {
        log.info("Refreshing access token using refresh token");
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        refreshTokenService.verifyExpiration(token);

        log.info("Generating new access token for user {}", token.getUser().getUsername());
        String accessToken = jwtTokenProvider.generateTokenFromUsername(token.getUser().getUsername());

        return new JwtResponse(
                accessToken,
                token.getToken(),
                token.getUser().getUsername(),
                token.getUser().getFullName(),
                List.of(token.getUser().getRole())
        );
    }

}
