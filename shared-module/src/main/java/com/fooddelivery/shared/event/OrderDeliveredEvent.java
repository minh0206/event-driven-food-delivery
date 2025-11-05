package com.fooddelivery.shared.event;

public record OrderDeliveredEvent(
        Long orderId,
        Long driverId) {
}
