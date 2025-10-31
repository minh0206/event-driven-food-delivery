package com.fooddelivery.shared.event;

import java.util.List;

// DTO for the overall order creation request
public record OrderPlacedEvent(
        Long orderId,
        Long restaurantId,
        List<OrderItemDetails> items) {
}
