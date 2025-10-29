package com.fooddelivery.userservice.controller;

import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.LoginResponseDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register/customer")
    public ResponseEntity<LoginResponseDto> registerCustomer(@Valid @RequestBody RegisterRequestDto request) {
        User registeredUser = userService.registerNewUser(userMapper.toUser(request), Role.CUSTOMER);
        String token = jwtService.generateToken(
                registeredUser.getId().toString(),
                Role.CUSTOMER.toString()
        );
        return new ResponseEntity<>(new LoginResponseDto(token, userMapper.toDto(registeredUser)), HttpStatus.CREATED);
    }

    @PostMapping("/register/restaurant")
    public ResponseEntity<LoginResponseDto> registerRestaurantAdmin(@Valid @RequestBody RegisterRequestDto request) {
        User registeredUser = userService.registerNewUser(userMapper.toUser(request), Role.CUSTOMER);
        String token = jwtService.generateToken(
                registeredUser.getId().toString(),
                Role.CUSTOMER.toString()
        );
        return new ResponseEntity<>(new LoginResponseDto(token, userMapper.toDto(registeredUser)), HttpStatus.CREATED);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<LoginResponseDto> registerDriver(@Valid @RequestBody RegisterRequestDto request) {
        User registeredUser = userService.registerNewUser(userMapper.toUser(request), Role.CUSTOMER);
        String token = jwtService.generateToken(
                registeredUser.getId().toString(),
                Role.CUSTOMER.toString()
        );
        return new ResponseEntity<>(new LoginResponseDto(token, userMapper.toDto(registeredUser)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        User user = userService.loginUser(requestDto.email(), requestDto.password());
        String token = jwtService.generateToken(user.getId().toString(), user.getRole().toString());
        return new LoginResponseDto(token, userMapper.toDto(user));
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userService.getUserById(userId);
        return userMapper.toDto(user);
    }
}
