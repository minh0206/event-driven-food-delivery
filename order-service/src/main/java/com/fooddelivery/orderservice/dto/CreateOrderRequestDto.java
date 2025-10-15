package com.fooddelivery.orderservice.dto;

import java.util.List;

// DTO for the overall order creation request
public record CreateOrderRequestDto(
        Long restaurantId,
        List<OrderItemDto> items
) {
}