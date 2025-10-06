package com.fooddelivery.restaurantservice.dto;

import java.math.BigDecimal;

public record MenuItemRequestDto(
        String name,
        String description,
        BigDecimal price) {
}

