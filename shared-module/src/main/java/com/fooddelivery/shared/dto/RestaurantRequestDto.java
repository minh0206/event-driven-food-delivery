package com.fooddelivery.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for creating/updating a restaurant
public record RestaurantRequestDto(
        @NotBlank(message = "Restaurant name is required") @Size(max = 255, message = "Restaurant name must be less than 255 characters") String restaurantName,
        String address,
        String cuisineType) {
}
