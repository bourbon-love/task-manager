package com.example.task_manager.dto;

import com.example.task_manager.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data returned to the client — never exposes the User entity directly.
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Task.Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private String ownerUsername;

    /** Builds a TaskResponse from a Task entity. */
    public static TaskResponse from(Task task) {
        TaskResponse r = new TaskResponse();
        r.id            = task.getId();
        r.title         = task.getTitle();
        r.description   = task.getDescription();
        r.completed     = task.getCompleted();
        r.priority      = task.getPriority();
        r.dueDate       = task.getDueDate();
        r.createdAt     = task.getCreatedAt();
        r.ownerUsername = task.getOwner().getUsername();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getCompleted() { return completed; }
    public Task.Priority getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getOwnerUsername() { return ownerUsername; }
}