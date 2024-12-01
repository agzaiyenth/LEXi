package com.lexi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Captures refresh token for requesting a new access token
 */
@Getter
@Setter
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
