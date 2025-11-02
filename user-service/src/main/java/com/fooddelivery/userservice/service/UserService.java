package com.fooddelivery.userservice.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.RestaurantRegisterRequestDto;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestaurantServiceClient restaurantServiceClient;
    private final DeliveryServiceClient deliveryServiceClient;

    private User registerUser(RegisterRequestDto requestDto, Role role) throws EmailExistsException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new EmailExistsException();
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword())); // Hash password
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setRole(role); // Set role

        return userRepository.save(user);
    }

    private void setAuthentication(User user) {
        // Extract the role claim and convert it to a collection of GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getId(), null, authorities));
    }

    public User registerCustomer(RegisterRequestDto requestDto) throws EmailExistsException {
        return registerUser(requestDto, Role.CUSTOMER);
    }

    @Transactional
    public User registerRestaurantAdmin(RestaurantRegisterRequestDto requestDto) throws EmailExistsException {
        User registeredUser = registerUser(requestDto, Role.RESTAURANT_ADMIN);
        setAuthentication(registeredUser); // Set authentication to generate JWT in FeignClientAuthInterceptor

        Long restaurantId = restaurantServiceClient.createRestaurant(requestDto).get("restaurantId");
        registeredUser.setRestaurantId(restaurantId);

        return userRepository.save(registeredUser);
    }

    @Transactional
    public User registerDriver(RegisterRequestDto requestDto) throws EmailExistsException {
        User registeredUser = registerUser(requestDto, Role.DELIVERY_DRIVER);
        setAuthentication(registeredUser); // Set authentication to generate JWT in FeignClientAuthInterceptor

        Long driverId = deliveryServiceClient.createDriver().get("driverId");
        registeredUser.setDriverId(driverId);

        return userRepository.save(registeredUser);
    }

    public User loginUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password));
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
