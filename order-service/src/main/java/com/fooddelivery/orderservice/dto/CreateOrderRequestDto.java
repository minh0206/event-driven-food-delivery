package com.fooddelivery.orderservice.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// DTO for the overall order creation request
public record CreateOrderRequestDto(
        @NotNull Long restaurantId,
        @Valid @NotEmpty List<OrderItemDto> items) {
}
