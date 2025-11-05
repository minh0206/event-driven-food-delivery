package com.fooddelivery.shared.enumerate;

public enum OrderStatus {
    PENDING, // Order placed, waiting for restaurant confirmation
    ACCEPTED, // Restaurant accepted the order
    REJECTED, // Restaurant rejected the order
    READY_FOR_PICKUP, // Order is ready for pickup
    DRIVER_ASSIGNED, // Driver has been assigned to the order
    IN_TRANSIT, // Order is in transit
    DELIVERED, // Order has been delivered
    CANCELLED // Order cancelled by the user
}