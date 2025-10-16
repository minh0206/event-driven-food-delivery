package com.fooddelivery.orderservice.dto;

// This object will be sent as the JSON payload in the WebSocket message.
public record OrderStatusUpdateDto(
        Long orderId,
        String status,
        String description
) {
}