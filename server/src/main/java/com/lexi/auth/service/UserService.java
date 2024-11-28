package com.lexi.auth.service;

import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import com.lexi.common.exception.GlobalExceptionHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public void invalidateToken(String token) {
        Optional<User> user = userRepository.findByRefreshToken(token);
        if (user.isPresent()) {
            user.get().setRefreshToken(null);
            userRepository.save(user.get());
            logger.info("Token invalidated for user: {}", user.get().getUsername());
        }
    }
}
