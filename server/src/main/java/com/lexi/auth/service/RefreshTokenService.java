package com.lexi.auth.service;

import com.lexi.auth.config.JwtProperties;
import com.lexi.auth.exception.TokenRefreshException;
import com.lexi.auth.model.RefreshToken;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.RefreshTokenRepository;
import com.lexi.auth.repository.UserRepository;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages refresh token creation, validation, and deletion.
 */
@Service
@Slf4j
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(Long userId) {
        log.info("Creating refresh token for user {}", userId);
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        log.info("Finding refresh token by token: {}", token);

        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            log.info("Refresh token has been revoked");
            throw new TokenRefreshException("Refresh token has been revoked.");
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            log.info("Refresh token has expired");
            throw new TokenRefreshException("Refresh token has expired. Please sign in again.");
        }
        return token;
    }


    @Transactional
    public void deleteByUserId(Long userId) {
        log.info("Deleting refresh token by user id: {}", userId);
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));
        log.info("Revoking refresh token");
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
    @Transactional
    public void revokeAllTokensForUser(Long userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser_Id(userId);
        log.info("Revoking all refresh tokens for user");
        tokens.forEach(token -> {
            token.setRevoked(true);
        });

        refreshTokenRepository.saveAll(tokens);
    }



}
