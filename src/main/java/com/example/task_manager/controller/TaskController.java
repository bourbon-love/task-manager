package com.example.task_manager.controller;

import com.example.task_manager.config.OpenApiConfig;
import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for task CRUD operations.
 * All endpoints require a valid JWT — Spring Security enforces this.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = OpenApiConfig.TAG_TASKS)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** GET /api/tasks — returns all tasks owned by the current user. */
    @Operation(summary = "自分のタスク一覧を取得")
    @GetMapping
    public List<TaskResponse> getAllTasks(@AuthenticationPrincipal String username) {
        return taskService.getAllTasks(username);
    }

    /** POST /api/tasks — creates a new task for the current user. */
    @Operation(summary = "タスクを作成")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Validated(TaskRequest.OnCreate.class) @RequestBody TaskRequest request,
            @AuthenticationPrincipal String username) {
        TaskResponse created = taskService.createTask(request, username);
        return ResponseEntity.status(201).body(created);
    }

    /** PUT /api/tasks/{id} — updates a task owned by the current user. */
    @Operation(summary = "タスクを更新（指定した項目のみ）")
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal String username) {
        return taskService.updateTask(id, request, username);
    }

    /** DELETE /api/tasks/{id} — deletes a task owned by the current user. */
    @Operation(summary = "タスクを削除")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        taskService.deleteTask(id, username);
        return ResponseEntity.noContent().build();
    }
}