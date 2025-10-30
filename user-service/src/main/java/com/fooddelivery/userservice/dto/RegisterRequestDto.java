package com.fooddelivery.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        @NotBlank(message = "First name is required") @Size(max = 255, message = "First name must be less than 255 characters") String firstName,
        @NotBlank(message = "Last name is required") @Size(max = 255, message = "Last name must be less than 255 characters") String lastName) {
}