package com.fooddelivery.restaurantservice.dto;

// DTO for sending restaurant data to the client
public record RestaurantDto(
        Long id,
        String restaurantName,
        String address,
        String cuisineType) {
}
