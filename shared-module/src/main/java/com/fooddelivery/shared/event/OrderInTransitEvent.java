package com.fooddelivery.shared.event;

public record OrderInTransitEvent(
        Long orderId,
        Long driverId,
        Long driverUserId) {
}
