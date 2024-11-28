package com.lexi.auth.controller;

import com.lexi.auth.dto.JwtResponse;
import com.lexi.auth.dto.LoginRequest;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import com.lexi.auth.util.JwtUtil;
import com.lexi.common.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.lexi.auth.service.UserService;

@RestController
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

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        logger.info("Logging in user: {}", loginRequest.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        logger.info("User logged in successfully: {}", loginRequest.getUsername());
        logger.info("Generated token: {}", token);
        return new JwtResponse(token);
    }

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.error("Username already exists: {}", user.getUsername());
            return "Username already exists!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
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
            logger.error("Invalid token format.");
            return ResponseEntity.badRequest().body("Invalid token format.");
        }

        String token = authorizationHeader.substring(7);
        try {
            jwtUtil.validateToken(token); // Validate if the token is well-formed
            userService.invalidateToken(token); // Invalidate the token (implementation in the service)
            logger.info("Logged out successfully.");
            return ResponseEntity.ok("Logged out successfully.");
        } catch (Exception e) {
            logger.error("Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }
}
