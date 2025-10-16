package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.model.DriverStatus;

public record UpdateStatusRequestDto(DriverStatus newStatus) {
}