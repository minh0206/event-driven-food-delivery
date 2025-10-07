package com.fooddelivery.userservice.controller;

import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto user = userService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto requestDto) {
        UserDto user = userService.loginUser(requestDto);
        var token = jwtService.generateToken(user.email(), user.id());
        return Map.of("token", token);
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.getUserById(userId);
    }
}
