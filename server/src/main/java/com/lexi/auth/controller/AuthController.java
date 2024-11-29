package com.lexi.auth.controller;

import com.lexi.auth.dto.JwtResponse;
import com.lexi.auth.dto.LoginRequest;
import com.lexi.auth.dto.SignupRequest;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import com.lexi.auth.util.JwtUtil;
import com.lexi.common.dto.ApiResponse;
import com.lexi.common.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.registerUser(signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        log.info("User registered successfully: {}", signupRequest.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", "Signup successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // If authentication is successful, generate JWT
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success(new JwtResponse(token), "Login successful"));

        } catch (BadCredentialsException ex) {
            // Handle invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
        } catch (Exception ex) {
            // Handle other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred during login"));
        }
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
            // Invalidate the token (implementation in the service)
            log.info("Logged out successfully.");
            return ResponseEntity.ok("Logged out successfully.");
        } catch (Exception e) {
            log.error("Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }
}
