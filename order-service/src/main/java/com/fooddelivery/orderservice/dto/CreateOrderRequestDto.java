package com.fooddelivery.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// DTO for the overall order creation request
public record CreateOrderRequestDto(
        @NotNull
        Long restaurantId,

        @Valid
        @NotEmpty
        List<OrderItemDto> items
) {
}