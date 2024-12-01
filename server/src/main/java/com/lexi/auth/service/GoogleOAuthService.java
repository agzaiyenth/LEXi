package com.lexi.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.lexi.auth.model.User;

/**
 * Handles Google OAuth flow and user authentication/creation.
 */
@Service
public class GoogleOAuthService {
    @Value("${google.oauth.clientId}")
    private String clientId;

    @Value("${google.oauth.clientSecret}")
    private String clientSecret;

    @Value("${google.oauth.redirectUri}")
    private String redirectUri;

    @Value("${google.oauth.tokenUri}")
    private String tokenUri;

    @Value("${google.oauth.userInfoUri}")
    private String userInfoUri;

//    TODO : GOOGLE AUTH ISNT IMPLEMENTED YET
    public String getGoogleAuthUrl() {
        return "https://accounts.google.com/o/oauth2/auth?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code&scope=email%20profile";
    }

    public String exchangeCodeForAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenResponse = restTemplate.postForObject(tokenUri, null, String.class);
        // Extract and return access token from the response
        return tokenResponse;
    }

    public User getUserInfoFromGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoResponse = restTemplate.getForObject(userInfoUri + "?access_token=" + accessToken, String.class);
        // Parse response and return user info
        return null;
    }
}
