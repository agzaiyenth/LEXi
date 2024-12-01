package com.lexi.auth.controller;

import com.lexi.auth.model.User;
import com.lexi.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lexi.auth.service.GoogleOAuthService;

/**
 * Manages Google OAuth endpoints.
 */
@RestController
@RequestMapping("/oauth")
public class GoogleOAuthController {
    @Autowired
    private GoogleOAuthService googleOAuthService;

    @GetMapping("/google")
    public ResponseEntity<?> redirectToGoogleAuth() {
        String googleAuthUrl = googleOAuthService.getGoogleAuthUrl();
        return ResponseEntity.ok(ApiResponse.success(googleAuthUrl, "Redirect URL generated successfully"));
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        String accessToken = googleOAuthService.exchangeCodeForAccessToken(code);
        User googleUser = googleOAuthService.getUserInfoFromGoogle(accessToken);
        // TODO: Handle user authentication or creation
        return ResponseEntity.ok(ApiResponse.success(googleUser,"User authenticated successfully"));
    }
}
