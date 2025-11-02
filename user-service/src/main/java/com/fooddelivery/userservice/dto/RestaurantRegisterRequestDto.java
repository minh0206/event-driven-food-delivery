package com.fooddelivery.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RestaurantRegisterRequestDto extends RegisterRequestDto {
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 255, message = "Restaurant name must be less than 255 characters")
    String restaurantName;
    String address;
    String cuisineType;
}
