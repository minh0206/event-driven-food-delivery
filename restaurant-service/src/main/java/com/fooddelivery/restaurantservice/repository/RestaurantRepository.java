package com.fooddelivery.restaurantservice.repository;

import com.fooddelivery.restaurantservice.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Find a restaurant owned by a specific user
    Optional<Restaurant> findByOwnerId(Long ownerId);
}