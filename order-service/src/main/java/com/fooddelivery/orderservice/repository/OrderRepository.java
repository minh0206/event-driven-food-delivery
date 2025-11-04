package com.fooddelivery.orderservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.shared.enumerate.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @EntityGraph(attributePaths = "items")
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(Long orderId);

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByRestaurantIdAndStatusIn(Long restaurantId, Collection<OrderStatus> statuses);

    List<Order> findByRestaurantIdAndStatusNotIn(Long restaurantId, Collection<OrderStatus> statuses);
}