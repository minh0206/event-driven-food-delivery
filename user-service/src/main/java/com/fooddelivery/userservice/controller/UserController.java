package com.fooddelivery.userservice.controller;

import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.LoginResponseDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.service.JwtService;
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


    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto user = userService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        var user = userService.loginUser(request);
        var token = jwtService.generateToken(request.email());
        return new LoginResponseDto(token);
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        var email = userDetails.getUsername();
        return userService.getUser(email);
    }
}
