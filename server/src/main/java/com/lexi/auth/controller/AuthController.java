package com.lexi.auth.controller;

import com.lexi.auth.dto.JwtResponse;
import com.lexi.auth.dto.LoginRequest;
import com.lexi.auth.dto.SignupRequest;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import com.lexi.auth.util.JwtUtil;
import com.lexi.common.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.lexi.auth.service.UserService;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        log.info("User logged in successfully: {}", loginRequest.getUsername());
        log.info("Generated token: {}", token);
        return new JwtResponse(token);
    }


    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequest signupRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            log.error("Username already exists: {}", signupRequest.getUsername());
            throw new RuntimeException("Username already exists!");
        }

        // Map SignupRequest to User entity
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setRole(User.Role.ROLE_USER); // Default role for new users

        // Save user in the database
        userRepository.save(user);
        log.info("User registered successfully: {}", signupRequest.getUsername());

        return "User registered successfully!";
    }


    /**
     * Logs out the user by invalidating the refresh token or recording the JWT token as invalid.
     *
     * @param request the HTTP request containing the Authorization header
     * @return a ResponseEntity indicating the result
     */
    /**
     * Logs out the user by invalidating the refresh token or recording the JWT token as invalid.
     *
     * @param request the HTTP request containing the Authorization header
     * @return a ResponseEntity indicating the result
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Invalid token format.");
            return ResponseEntity.badRequest().body("Invalid token format.");
        }

        String token = authorizationHeader.substring(7);
        try {
            jwtUtil.validateToken(token); // Validate if the token is well-formed
            userService.invalidateToken(token); // Invalidate the token (implementation in the service)
            log.info("Logged out successfully.");
            return ResponseEntity.ok("Logged out successfully.");
        } catch (Exception e) {
            log.error("Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }
}
