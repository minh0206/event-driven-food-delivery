package com.fooddelivery.userservice.dto;

import java.time.LocalDateTime;

public record TokenDto(
        Long id,
        String refreshToken,
        Long userId,
        LocalDateTime expiresAt,
        boolean revoked,
        LocalDateTime createdAt,
        LocalDateTime revokedAt) {
}
