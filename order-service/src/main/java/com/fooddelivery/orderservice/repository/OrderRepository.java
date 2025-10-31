package com.fooddelivery.orderservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.shared.enumerate.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByRestaurantIdAndStatusIn(Long restaurantId, Collection<OrderStatus> statuses);

    List<Order> findByRestaurantIdAndStatusNotIn(Long restaurantId, Collection<OrderStatus> statuses);
}