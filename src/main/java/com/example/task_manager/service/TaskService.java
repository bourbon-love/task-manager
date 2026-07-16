package com.example.task_manager.service;

import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Business logic for task management with per-user ownership enforcement.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /** Returns all tasks owned by the given user. */
    public List<TaskResponse> getAllTasks(String username) {
        User owner = findUser(username);
        return taskRepository.findByOwner(owner)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    /** Creates a new task owned by the given user. */
    public TaskResponse createTask(TaskRequest request, String username) {
        User owner = findUser(username);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        task.setPriority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setOwner(owner);

        return TaskResponse.from(taskRepository.save(task));
    }

    /** Updates a task — only if it belongs to the given user. */
    public TaskResponse updateTask(Long id, TaskRequest request, String username) {
        User owner = findUser(username);
        Task task = taskRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (request.getTitle() != null)     task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getCompleted() != null) task.setCompleted(request.getCompleted());
        if (request.getPriority() != null)  task.setPriority(request.getPriority());
        if (request.getDueDate() != null)   task.setDueDate(request.getDueDate());

        return TaskResponse.from(taskRepository.save(task));
    }

    /** Deletes a task — only if it belongs to the given user. */
    public void deleteTask(Long id, String username) {
        User owner = findUser(username);
        Task task = taskRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(task);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}