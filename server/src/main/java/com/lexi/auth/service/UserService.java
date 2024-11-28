package com.lexi.auth.service;

import com.lexi.auth.exception.EmailAlreadyExistsException;
import com.lexi.auth.exception.UsernameAlreadyExistsException;
import com.lexi.auth.exception.InvalidPasswordException;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("The email address is already in use.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.ROLE_USER);

        userRepository.save(user);
    }

    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.debug("Raw password provided: {}", password);
        log.debug("Encoded password from DB: {}", user.getPassword());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.debug("Password mismatch");
            throw new InvalidPasswordException("Invalid password.");
        }

        log.debug("Password matches");
        return user;
    }

}
