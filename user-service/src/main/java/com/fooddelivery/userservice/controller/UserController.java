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

    @PostMapping("/register/customer")
    public ResponseEntity<UserDto> registerCustomer(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerCustomer(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/register/restaurant")
    public ResponseEntity<UserDto> registerRestaurantAdmin(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerRestaurantAdmin(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<UserDto> registerDriver(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerDriver(request);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto requestDto) {
        UserDto userDto = userService.loginUser(requestDto);
        var token = jwtService.generateToken(userDto.id().toString(), userDto.role().name());
        return Map.of("token", token);
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.getUserById(userId);
    }
}
