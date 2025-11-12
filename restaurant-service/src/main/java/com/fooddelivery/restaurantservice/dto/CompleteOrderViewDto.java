package com.fooddelivery.restaurantservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fooddelivery.shared.dto.OrderItemDto;

public record CompleteOrderViewDto(
        // Fields from Master
        Long orderId,
        Long restaurantId,
        Long customerId,
        Long driverUserId,
        String finalStatus,
        List<OrderItemDto> items,
        BigDecimal totalPrice,
        LocalDateTime deliveryTimestamp,

        // Fields from Operational
        String assignedCook,
        String internalNotes) {
}