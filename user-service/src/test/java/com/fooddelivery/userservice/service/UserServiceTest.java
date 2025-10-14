package com.fooddelivery.userservice.service;

import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDto registerRequest;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Common setup for tests
        registerRequest = new RegisterRequestDto(
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        // Prepare a userDto object that our mock mapper will return
        userDto = new UserDto(
                1L,
                "test@example.com",
                "Test",
                "User",
                Role.CUSTOMER);
    }

    // Test Case 1: The "Happy Path" for successful registration
    @Test
    void whenRegisterUser_withNewEmail_shouldSaveAndReturnUser() {
        // --- Arrange (Given) ---
        // 1. Define the behavior of the mocks
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // --- Act (When) ---
        // 2. Call the method we are testing
        UserDto savedUser = userService.registerCustomer(registerRequest);

        // --- Assert (Then) ---
        // 3. Verify the results and interactions
        assertNotNull(savedUser);
        // Verify that the save method on the repository was called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test Case 2: The "Unhappy Path" where the user email already exists
    @Test
    void whenRegisterUser_withExistingEmail_shouldThrowException() {
        // --- Arrange (Given) ---
        // 1. Make the mock repository return a user, simulating that the email is already taken
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(new User()));

        // --- Act & Assert (When & Then) ---
        // 2. Assert that calling the method throws the expected exception
        EmailExistsException exception = assertThrows(EmailExistsException.class, () -> {
            userService.registerCustomer(registerRequest);
        });

        // 3. Verify that the save method was never called because of the exception
        verify(userRepository, never()).save(any(User.class));
    }

    // Test Case 3: Login user with valid credentials
    @Test
    void whenLoginUser_withValidCredentials_shouldReturnUser() {
        // --- Arrange (Given) ---
        // 1. Make the mock repository return a user
        var loginRequest = new LoginRequestDto(registerRequest.email(), registerRequest.password());

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(new User()));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // --- Act (When) ---
        // 2. Call the method we are testing
        UserDto returnedUserDto = userService.loginUser(loginRequest);

        // --- Assert (Then) ---
        // 3. Verify the results and interactions
        assertEquals(returnedUserDto, userDto);
    }

    // Test Case 4: Get user by id
    @Test
    void whenGetUserById_shouldReturnUser() {
        // --- Arrange (Given) ---
        // 1. Make the mock repository return a user
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // --- Act (When) ---
        // 2. Call the method we are testing
        UserDto returnedUserDto = userService.getUserById(1L);

        // --- Assert (Then) ---
        // 3. Verify the results and interactions
        assertEquals(returnedUserDto, userDto);

        // Verify that the repository's `findById` method was called exactly once
        verify(userRepository, times(1)).findById(1L);
    }
}