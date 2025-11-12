package com.fooddelivery.shared.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Represents the data coming from the order-service.
public record MasterOrderDto(
        Long orderId,
        Long restaurantId,
        Long customerId,
        Long driverUserId,
        String finalStatus,
        List<OrderItemDto> items,
        BigDecimal totalPrice,
        LocalDateTime deliveryTimestamp) {
}