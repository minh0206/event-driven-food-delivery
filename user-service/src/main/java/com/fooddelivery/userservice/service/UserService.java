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

import com.fooddelivery.shared.dto.RestaurantRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
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

    private User registerUser(RegisterRequestDto requestDto, Role role) throws EntityExistsException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(requestDto.email());
        user.setPassword(passwordEncoder.encode(requestDto.password())); // Hash password
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
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

    public User registerCustomer(RegisterRequestDto requestDto) throws EntityExistsException {
        return registerUser(requestDto, Role.CUSTOMER);
    }

    @Transactional
    public User registerRestaurantAdmin(RegisterRequestDto requestDto) throws EntityExistsException {
        User registeredUser = registerUser(requestDto, Role.RESTAURANT_ADMIN);
        setAuthentication(registeredUser); // Set authentication to generate JWT in FeignClientAuthInterceptor

        RestaurantRequestDto restaurantRequestDto = new RestaurantRequestDto(
                requestDto.restaurantName(),
                requestDto.address(),
                requestDto.cuisineType());

        Long restaurantId = restaurantServiceClient.createRestaurant(restaurantRequestDto).get("restaurantId");
        registeredUser.setRestaurantId(restaurantId);

        return userRepository.save(registeredUser);
    }

    @Transactional
    public User registerDriver(RegisterRequestDto requestDto) throws EntityExistsException {
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
