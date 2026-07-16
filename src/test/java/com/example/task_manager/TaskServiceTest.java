package com.example.task_manager;

import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User bob;

    @BeforeEach
    void setUp() {
        bob = new User();
        bob.setUsername("bob");
        bob.setPassword("hashed");
    }

    @Test
    void createTask_success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setPriority(Task.Priority.HIGH);

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Test Task");
        saved.setPriority(Task.Priority.HIGH);
        saved.setCompleted(false);
        saved.setOwner(bob);

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse response = taskService.createTask(request, "bob");

        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(response.getOwnerUsername()).isEqualTo("bob");
    }

    @Test
    void updateTask_notFound_throws404() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(taskRepository.findByIdAndOwner(99L, bob)).thenReturn(Optional.empty());

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated");

        assertThatThrownBy(() -> taskService.updateTask(99L, request, "bob"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void deleteTask_crossUser_throws404() {
        User alice = new User();
        alice.setUsername("alice");
        alice.setPassword("hashed");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(taskRepository.findByIdAndOwner(1L, alice)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L, "alice"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Task not found");
    }
}