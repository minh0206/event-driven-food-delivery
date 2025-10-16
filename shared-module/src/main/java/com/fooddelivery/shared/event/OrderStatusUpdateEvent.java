package com.fooddelivery.shared.event;

// This event is sent back to the order-service
public record OrderStatusUpdateEvent(
        Long orderId,
        String newStatus
) {
}