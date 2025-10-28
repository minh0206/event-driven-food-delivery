package com.fooddelivery.orderservice.repository;

import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByRestaurantId(Long restaurantId);

    @Query("select o from Order o where o.restaurantId = ?1 and o.status in ?2")
    List<Order> findByRestaurantIdAndStatusIn(Long restaurantId, Collection<OrderStatus> statuses);

    @Query("select o from Order o where o.restaurantId = ?1 and o.status not in ?2")
    List<Order> findByRestaurantIdAndStatusNotIn(Long restaurantId, Collection<OrderStatus> statuses);
}