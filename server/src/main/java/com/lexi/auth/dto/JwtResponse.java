package com.lexi.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Returns JWT tokens and user details upon successful login or token refresh.
 */
@Getter
@Setter
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private List<String> roles;

    public JwtResponse(String accessToken, String refreshToken, String username, String fullName, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.fullName = fullName;
        this.roles = roles;
    }
}

