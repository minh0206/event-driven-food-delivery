package com.fooddelivery.deliveryservice.dto;

public record LocationUpdateRequestDto(
        double latitude,
        double longitude
) {
}
