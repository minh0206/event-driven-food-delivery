package com.fooddelivery.userservice.dto;

import com.fooddelivery.userservice.model.Role;

public record UserDto(Long id, String email, String firstName, String lastName, Role role) {
}
