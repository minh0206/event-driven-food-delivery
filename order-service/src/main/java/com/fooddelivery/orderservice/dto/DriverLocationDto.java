package com.fooddelivery.orderservice.dto;

public record DriverLocationDto(
        Long orderId,
        double latitude,
        double longitude) {
}
