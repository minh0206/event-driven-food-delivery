package com.fooddelivery.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fooddelivery.shared.dto.OrderItemDto;
import com.fooddelivery.shared.enumerate.OrderStatus;

public record CustomerOrderDto(
        Long id,
        Long customerId,
        Long restaurantId,
        Long driverUserId,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemDto> items,
        LocalDateTime createdAt) {
}
