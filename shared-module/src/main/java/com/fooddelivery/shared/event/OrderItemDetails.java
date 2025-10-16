package com.fooddelivery.shared.event;

// DTO representing a single item in the order request
public record OrderItemDetails(
        Long menuItemId,
        int quantity
) {
}
