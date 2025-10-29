package com.fooddelivery.shared.enumerate;

public enum OrderStatus {
    PENDING,       // Order placed, waiting for restaurant confirmation
    ACCEPTED,      // Restaurant accepted the order
    REJECTED,      // Restaurant rejected the order
    PREPARING,     // Order is being prepared
    READY_FOR_PICKUP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED      // Order cancelled by the user
}