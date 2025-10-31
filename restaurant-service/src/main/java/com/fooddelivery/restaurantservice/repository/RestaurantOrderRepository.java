package com.fooddelivery.restaurantservice.repository;

import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    @EntityGraph(attributePaths = "items")
    List<RestaurantOrder> findByRestaurantId(Long restaurantId);
}