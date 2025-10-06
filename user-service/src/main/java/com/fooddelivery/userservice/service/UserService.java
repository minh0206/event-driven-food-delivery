package com.fooddelivery.userservice.service;

import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.exception.EmailExistsException;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
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
    private final AuthenticationManager authenticationManager;

    public UserDto registerUser(RegisterRequestDto request) throws EmailExistsException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailExistsException();
        }
        var user = userMapper.toUser(request); // Convert RegisterRequestDto to User

        user.setPassword(passwordEncoder.encode(request.password())); // Hash password
        user.setRole(Role.CUSTOMER); // Default role
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto loginUser(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return getUser(request.email());
    }

    public UserDto getUser(String email) {
        var user = userRepository.findByEmail(email).orElseThrow();
        return userMapper.toDto(user);
    }
}
