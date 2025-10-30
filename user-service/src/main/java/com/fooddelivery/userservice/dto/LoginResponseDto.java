package com.fooddelivery.userservice.dto;

public record LoginResponseDto(
        String token,
        UserDto user) {
}
