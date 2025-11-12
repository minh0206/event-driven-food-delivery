package com.fooddelivery.restaurantservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.shared.enumerate.OrderStatus;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    @EntityGraph(attributePaths = "items")
    List<RestaurantOrder> findByRestaurantIdOrderByReceivedAtDesc(Long restaurantId);

    @EntityGraph(attributePaths = "items")
    List<RestaurantOrder> findByRestaurantIdAndLocalStatusNot(Long restaurantId, OrderStatus localStatus);

    @EntityGraph(attributePaths = "items")
    Page<RestaurantOrder> findByRestaurantIdAndLocalStatus(Long restaurantId, OrderStatus localStatus,
            Pageable pageable);
}