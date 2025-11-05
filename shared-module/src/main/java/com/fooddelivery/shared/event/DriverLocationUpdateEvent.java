package com.fooddelivery.shared.event;

public record DriverLocationUpdateEvent(
        Long orderId,
        Long driverId,
        double latitude,
        double longitude) {
}