package com.fooddelivery.userservice.dto;

import java.time.LocalDateTime;

public record RefreshTokenDto(
        Long id,
        String token,
        Long userId,
        String userEmail,
        String userRole,
        LocalDateTime expiresAt,
        boolean revoked,
        LocalDateTime createdAt,
        LocalDateTime revokedAt) {
}
