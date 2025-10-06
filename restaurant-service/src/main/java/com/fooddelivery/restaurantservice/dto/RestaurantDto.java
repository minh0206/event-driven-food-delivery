package com.fooddelivery.restaurantservice.dto;

// DTO for sending restaurant data to the client
public record RestaurantDto(
        Long id,
        String name,
        String address,
        String cuisineType,
        Long ownerId) {
}

