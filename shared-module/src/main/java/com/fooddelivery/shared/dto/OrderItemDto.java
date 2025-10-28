package com.fooddelivery.shared.dto;

// DTO representing a single item in the order request
public record OrderItemDto(
        Long menuItemId,
        int quantity
) {
}
