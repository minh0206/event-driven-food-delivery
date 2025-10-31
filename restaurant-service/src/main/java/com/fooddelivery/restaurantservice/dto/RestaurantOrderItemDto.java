package com.fooddelivery.restaurantservice.dto;

public record RestaurantOrderItemDto(
        Long menuItemId,
        Long quantity) {
}
