package com.example.task_manager.controller;

import com.example.task_manager.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Simple controller to verify the application is running.
 */
@RestController
@Tag(name = OpenApiConfig.TAG_HEALTH)
public class HelloController {

    /**
     * Returns a greeting message.
     *
     * @return JSON response with a message field
     */
    @Operation(summary = "動作確認")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello, World!");
    }
}
