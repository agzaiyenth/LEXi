package com.lexi.auth.service;

import com.lexi.auth.exception.EmailAlreadyExistsException;
import com.lexi.auth.exception.UsernameAlreadyExistsException;
import com.lexi.auth.exception.InvalidPasswordException;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;

import java.util.Optional;

/**
 *  business logic for user-related operations.
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        log.info("Checking if user exists by username: {}", username);
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        log.info("Checking if user exists by email: {}", email);
        return userRepository.existsByEmail(email);
    }


    @CacheEvict(value = "users", key = "#user.username")
    public User saveUser(User user) {
        log.info("Saving user: {}", user);
        return userRepository.save(user);
    }
}
