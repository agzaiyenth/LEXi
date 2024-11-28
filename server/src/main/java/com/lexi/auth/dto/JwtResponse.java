package com.lexi.auth.dto;

import lombok.Getter;

@Getter
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

}

