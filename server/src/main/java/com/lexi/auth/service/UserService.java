package com.lexi.auth.service;

import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public void invalidateToken(String token) {
        // Assuming you store refresh tokens in the database
        Optional<User> user = userRepository.findByRefreshToken(token);
        if (user.isPresent()) {
            user.get().setRefreshToken(null); // Clear the refresh token
            userRepository.save(user.get());
        }
    }
}
