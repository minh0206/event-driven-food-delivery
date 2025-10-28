package com.fooddelivery.shared.event;

import com.fooddelivery.shared.dto.OrderItemDto;

import java.util.List;

// DTO for the overall order creation request
public record OrderPlacedEvent(
        Long orderId,
        Long restaurantId,
        Long customerId,
        List<OrderItemDto> items
) {
}
