package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.model.DriverStatus;

public record DriverDto(
        Long id,
        Long userId,
        DriverStatus status,
        Double currentLatitude,
        Double currentLongitude,
        Long currentOrderId) {
}
