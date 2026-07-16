package com.example.task_manager.service;

import com.example.task_manager.entity.User;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user registration and login logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user with an encrypted password.
     *
     * @param username the desired username
     * @param password the plain-text password to encrypt and store
     * @return the saved user, or null if the username is already taken
     */
    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Verifies credentials and issues a JWT on success.
     *
     * @param username the username to look up
     * @param password the plain-text password to verify
     * @return a signed JWT, or null if credentials are invalid
     */
    public String login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getUsername()))
                .orElse(null);
    }
}
