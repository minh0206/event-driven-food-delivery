package com.fooddelivery.userservice.repository;

import com.fooddelivery.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by email
    Optional<User> findByEmail(String email);
}
