package com.example.task_manager.controller;

import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for task CRUD operations.
 * All endpoints require a valid JWT — Spring Security enforces this.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** GET /api/tasks — returns all tasks owned by the current user. */
    @GetMapping
    public List<TaskResponse> getAllTasks(@AuthenticationPrincipal String username) {
        return taskService.getAllTasks(username);
    }

    /** POST /api/tasks — creates a new task for the current user. */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal String username) {
        TaskResponse created = taskService.createTask(request, username);
        return ResponseEntity.status(201).body(created);
    }

    /** PUT /api/tasks/{id} — updates a task owned by the current user. */
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal String username) {
        return taskService.updateTask(id, request, username);
    }

    /** DELETE /api/tasks/{id} — deletes a task owned by the current user. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        taskService.deleteTask(id, username);
        return ResponseEntity.noContent().build();
    }
}