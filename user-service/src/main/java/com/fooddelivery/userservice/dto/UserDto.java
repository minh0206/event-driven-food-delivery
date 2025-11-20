package com.fooddelivery.userservice.dto;

import com.fooddelivery.userservice.model.Role;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
