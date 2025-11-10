package com.fooddelivery.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,

        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password,

        @NotBlank(message = "First name is required") @Size(max = 255, message = "First name must be less than 255 characters") String firstName,

        @Size(max = 255, message = "Last name must be less than 255 characters") String lastName,

        @Size(max = 255, message = "Restaurant name must be less than 255 characters") String restaurantName,

        @Size(max = 255, message = "Address must be less than 255 characters") String address,

        @Size(max = 255, message = "Cuisine type must be less than 255 characters") String cuisineType) {
}
