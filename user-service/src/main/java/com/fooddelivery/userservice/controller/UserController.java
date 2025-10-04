package com.fooddelivery.userservice.controller;

import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.LoginResponseDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto request) {
        try {
            userService.registerUser(request);
            return new ResponseEntity<>("User registered successfully!:", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("User registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        try {
            String token = userService.login(request);
            return new ResponseEntity<>(new LoginResponseDto(token), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
