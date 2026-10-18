package com.example.task_manager.dto;

import com.example.task_manager.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.time.LocalDate;

/**
 * Payload received from the client when creating or updating a task.
 * Updates are partial, so title is required only on create (OnCreate group).
 */
public class TaskRequest {

    /** Validation group for rules that apply only when creating a task. */
    public interface OnCreate extends Default {}

    @NotBlank(groups = OnCreate.class, message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Size(max = 255, message = "description must be at most 255 characters")
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
