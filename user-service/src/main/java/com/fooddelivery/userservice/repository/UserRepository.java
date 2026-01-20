package com.fooddelivery.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by email
    Optional<User> findByEmail(String email);

    Optional<User> findByRole(Role role);
}
