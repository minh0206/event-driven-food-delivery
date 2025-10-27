package com.fooddelivery.userservice.controller;

import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.LoginResponseDto;
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

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register/customer")
    public ResponseEntity<LoginResponseDto> registerCustomer(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerCustomer(request);
        var token = jwtService.generateToken(userDto.id().toString(), userDto.role().name());
        return new ResponseEntity<>(new LoginResponseDto(token, userDto), HttpStatus.CREATED);
    }

    @PostMapping("/register/restaurant")
    public ResponseEntity<LoginResponseDto> registerRestaurantAdmin(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerRestaurantAdmin(request);
        var token = jwtService.generateToken(userDto.id().toString(), userDto.role().name());
        return new ResponseEntity<>(new LoginResponseDto(token, userDto), HttpStatus.CREATED);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<LoginResponseDto> registerDriver(@Valid @RequestBody RegisterRequestDto request) {
        UserDto userDto = userService.registerDriver(request);
        var token = jwtService.generateToken(userDto.id().toString(), userDto.role().name());
        return new ResponseEntity<>(new LoginResponseDto(token, userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        UserDto userDto = userService.loginUser(requestDto);
        var token = jwtService.generateToken(userDto.id().toString(), userDto.role().name());
        return new LoginResponseDto(token, userDto);
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.getUserById(userId);
    }
}
