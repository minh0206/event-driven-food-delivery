package com.fooddelivery.restaurantservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fooddelivery.shared.enumerate.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Define the table and a composite index for fast lookups of active orders.
// This index is the key to performance for the live dashboard.
@Table(name = "restaurant_orders", indexes = {
        @Index(name = "idx_restaurant_status", columnList = "restaurantId, localStatus")
})
@Entity
@Getter
@Setter
public class RestaurantOrder {
    @Id
    private Long orderId; // Using the same ID from order-service

    @Column(nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus localStatus; // e.g., "PENDING", "PREPARING", "READY_FOR_PICKUP"

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantOrderItem> items = new ArrayList<>();

    private String assignedCook;

    @Column(columnDefinition = "TEXT")
    private String internalNotes;
}