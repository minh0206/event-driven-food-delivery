package com.fooddelivery.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fooddelivery.shared.enumerate.OrderStatus;

public record OrderDto(
        Long id,
        Long customerId,
        Long restaurantId,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemDto> items,
        LocalDateTime createdAt) {
}
