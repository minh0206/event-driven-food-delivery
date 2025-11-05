package com.fooddelivery.shared.event;

// This DTO represents the incoming event from restaurant service
public record OrderAcceptedEvent(
        Long orderId,
        Long restaurantId) {
}