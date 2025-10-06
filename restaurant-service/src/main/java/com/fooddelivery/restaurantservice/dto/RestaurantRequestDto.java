package com.fooddelivery.restaurantservice.dto;

// DTO for creating/updating a restaurant
public record RestaurantRequestDto(
        String name,
        String address,
        String cuisineType) {
}