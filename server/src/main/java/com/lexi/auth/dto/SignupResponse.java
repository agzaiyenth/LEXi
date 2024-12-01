package com.lexi.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * success message or detail return dto upon successful signup.
 */
@Getter
@Setter
public class SignupResponse {
    private String username;
    private String email;
    private String message;

    public SignupResponse(String username, String email, String message) {
        this.username = username;
        this.email = email;
        this.message = message;
    }
}
