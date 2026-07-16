package com.example.task_manager.dto;

import com.example.task_manager.entity.Task;
import java.time.LocalDate;

/**
 * Payload received from the client when creating or updating a task.
 */
public class TaskRequest {

    private String title;
    private String description;
    private Boolean completed;
    private Task.Priority priority;
    private LocalDate dueDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Task.Priority getPriority() { return priority; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}