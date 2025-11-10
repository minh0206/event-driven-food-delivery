package com.fooddelivery.userservice.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private static final String TOKEN_KEY = "token";
    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @PostMapping("/register/customer")
    public Map<String, String> registerCustomer(@Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerCustomer(requestDto);
        return Map.of(TOKEN_KEY, jwtService.generateToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString()));
    }

    @PostMapping("/register/restaurant")
    public Map<String, String> registerRestaurantAdmin(
            @Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerRestaurantAdmin(requestDto);
        return Map.of(TOKEN_KEY, jwtService.generateToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString()));
    }

    @PostMapping("/register/driver")
    public Map<String, String> registerDriver(@Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerDriver(requestDto);
        return Map.of(TOKEN_KEY, jwtService.generateToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString()));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto requestDto) {
        User user = userService.loginUser(requestDto.email(), requestDto.password());
        return Map.of(TOKEN_KEY, jwtService.generateToken(
                user.getId().toString(),
                user.getRole().toString()));
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        User user = userService.getUserById(userId);

        if (user.getRole() == Role.RESTAURANT_ADMIN) {
            return userMapper.toRestaurantAdminDto(user);
        } else if (user.getRole() == Role.DELIVERY_DRIVER) {
            return userMapper.toDriverDto(user);
        } else {
            return userMapper.toCustomerDto(user);
        }
    }
}
