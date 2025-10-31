package com.fooddelivery.restaurantservice.dto;

import com.fooddelivery.shared.enumerate.OrderStatus;

public record RestaurantOrderRequestDto(
        OrderStatus status,
        String assignedCook,
        String internalNotes) {
}
