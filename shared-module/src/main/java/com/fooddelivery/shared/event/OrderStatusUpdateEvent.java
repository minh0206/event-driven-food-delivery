package com.fooddelivery.shared.event;

import com.fooddelivery.shared.enumerate.OrderStatus;

// This event is sent back to the order-service
public record OrderStatusUpdateEvent(
        Long orderId,
        OrderStatus newStatus
) {
}