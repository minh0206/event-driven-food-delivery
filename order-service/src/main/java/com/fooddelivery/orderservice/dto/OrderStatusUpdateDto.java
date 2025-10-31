package com.fooddelivery.orderservice.dto;

import com.fooddelivery.shared.enumerate.OrderStatus;

public record OrderStatusUpdateDto(
        Long orderId,
        OrderStatus status) {
}
