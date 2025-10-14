package com.fooddelivery.userservice.service;

import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private UserDto registerNewUser(RegisterRequestDto request, Role role) throws EmailExistsException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailExistsException();
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // Hash password
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(role); // Set role

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto registerCustomer(RegisterRequestDto request) {
        return registerNewUser(request, Role.CUSTOMER);
    }

    public UserDto registerRestaurantAdmin(RegisterRequestDto request) {
        return registerNewUser(request, Role.RESTAURANT_ADMIN);
    }

    public UserDto registerDriver(RegisterRequestDto request) {
        return registerNewUser(request, Role.DELIVERY_DRIVER);
    }

    public UserDto loginUser(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        return userMapper.toDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return userMapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
