package com.lexi.auth.repository;

import com.lexi.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lexi.auth.model.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * Handles database operations for the RefreshToken entity
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
    List<RefreshToken> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);

}
