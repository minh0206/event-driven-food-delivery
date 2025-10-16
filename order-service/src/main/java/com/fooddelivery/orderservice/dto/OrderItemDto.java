package com.fooddelivery.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DTO representing a single item in the order request
public record OrderItemDto(
        @NotNull
        Long menuItemId,

        @Min(1)
        int quantity,

        @NotNull
        BigDecimal price
) {
}