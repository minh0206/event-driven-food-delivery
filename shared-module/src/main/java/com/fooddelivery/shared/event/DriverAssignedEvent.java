package com.fooddelivery.shared.event;

// This DTO is the outgoing event from delivery service
public record DriverAssignedEvent(
        Long orderId,
        Long driverId,
        Long driverUserId
) {
}