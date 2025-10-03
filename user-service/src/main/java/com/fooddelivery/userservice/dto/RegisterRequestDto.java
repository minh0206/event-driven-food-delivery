package com.fooddelivery.userservice.dto;

public record RegisterRequestDto(String email, String password, String firstName, String lastName) {
}