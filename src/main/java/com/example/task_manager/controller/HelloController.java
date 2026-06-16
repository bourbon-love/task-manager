package com.example.task_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Simple controller to verify the application is running.
 */
@RestController
public class HelloController {

    /**
     * Returns a greeting message.
     *
     * @return JSON response with a message field
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello, World!");
    }
}
