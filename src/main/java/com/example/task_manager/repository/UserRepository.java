package com.example.task_manager.repository;

import com.example.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for performing CRUD operations on User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Finds a user by username. Used during login to look up credentials. */
    Optional<User> findByUsername(String username);
}