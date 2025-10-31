package com.fooddelivery.restaurantservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurantservice.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Find a restaurant owned by a specific user
    Optional<Restaurant> findByOwnerId(Long ownerId);
}