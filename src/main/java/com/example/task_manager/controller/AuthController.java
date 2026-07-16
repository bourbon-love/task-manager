package com.example.task_manager.controller;

import com.example.task_manager.entity.User;
import com.example.task_manager.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register — creates a new user account. */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        User user = authService.register(body.get("username"), body.get("password"));
        if (user == null) {
            return ResponseEntity.status(409).body("Username already taken");
        }
        return ResponseEntity.status(201).body("User registered successfully");
    }

    /** POST /api/auth/login — authenticates a user and returns a JWT. */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("username"), body.get("password"));
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
        return ResponseEntity.ok(Map.of("token", token));
    }
}