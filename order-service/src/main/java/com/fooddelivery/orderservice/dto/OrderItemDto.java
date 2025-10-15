package com.fooddelivery.orderservice.dto;

import java.math.BigDecimal;

// DTO representing a single item in the order request
public record OrderItemDto(
        Long menuItemId,
        int quantity,
        BigDecimal price
) {
}