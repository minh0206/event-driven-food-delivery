package com.fooddelivery.shared.dto;

import java.time.LocalDateTime;

// Using a Java Record for an immutable, standard error response
public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}