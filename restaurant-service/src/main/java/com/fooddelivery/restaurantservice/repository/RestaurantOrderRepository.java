package com.fooddelivery.restaurantservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurantservice.model.RestaurantOrder;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    @EntityGraph(attributePaths = "items")
    List<RestaurantOrder> findByRestaurantIdOrderByReceivedAtDesc(Long restaurantId);
}