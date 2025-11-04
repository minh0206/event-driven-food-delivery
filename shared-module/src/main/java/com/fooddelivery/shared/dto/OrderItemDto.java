package com.fooddelivery.shared.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long menuItemId,
        int quantity,
        BigDecimal price) {
}
