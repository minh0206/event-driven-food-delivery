package com.fooddelivery.orderservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// DTO representing a single item in the order request
public record OrderItemRequestDto(
        @NotNull Long menuItemId,
        @Min(1) int quantity,
        @NotNull BigDecimal price) {
}
