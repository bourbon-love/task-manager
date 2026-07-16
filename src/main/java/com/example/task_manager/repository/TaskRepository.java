package com.example.task_manager.repository;

import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Task CRUD operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** Returns all tasks belonging to the given user. */
    List<Task> findByOwner(User owner);

    /** Returns a task by ID only if it belongs to the given user. */
    Optional<Task> findByIdAndOwner(Long id, User owner);
}