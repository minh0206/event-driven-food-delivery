package com.fooddelivery.restaurantservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fooddelivery.shared.enumerate.OrderStatus;

public record RestaurantOrderDto(
        Long orderId,
        OrderStatus status,
        LocalDateTime receivedAt,
        List<RestaurantOrderItemDto> items) {
}
