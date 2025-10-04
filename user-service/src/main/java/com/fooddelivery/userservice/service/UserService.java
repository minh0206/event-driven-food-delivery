package com.fooddelivery.userservice.service;

import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    public User registerUser(RegisterRequestDto request) throws IllegalStateException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }
        var user = userMapper.toUser(request); // Convert RegisterRequestDto to User

        user.setPassword(passwordEncoder.encode(request.password())); // Hash password
        user.setRole(Role.CUSTOMER); // Default role

        return userRepository.save(user);
    }

    public String login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        return jwtTokenProvider.generateToken(request.email());
    }
}
