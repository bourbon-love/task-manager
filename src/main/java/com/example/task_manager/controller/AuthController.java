package com.example.task_manager.controller;

import com.example.task_manager.config.OpenApiConfig;
import com.example.task_manager.dto.AuthRequest;
import com.example.task_manager.entity.User;
import com.example.task_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = OpenApiConfig.TAG_AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register — creates a new user account. */
    @Operation(summary = "ユーザー登録")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        User user = authService.register(request.getUsername(), request.getPassword());
        if (user == null) {
            return ResponseEntity.status(409).body("Username already taken");
        }
        return ResponseEntity.status(201).body("User registered successfully");
    }

    /** POST /api/auth/login — authenticates a user and returns a JWT. */
    @Operation(summary = "ログイン（JWTを発行）")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
        return ResponseEntity.ok(Map.of("token", token));
    }
}
