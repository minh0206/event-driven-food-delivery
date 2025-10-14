package com.fooddelivery.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.config.SharedModuleAutoConfiguration;
import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SharedModuleAutoConfiguration.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Common setup for tests
        // Prepare a userDto object that our mock mapper will return
        userDto = new UserDto(
                1L,
                "test@example.com",
                "Test",
                "User",
                Role.CUSTOMER);
    }

    // Test Case 1: Successful registration request
    @Test
    void whenPostRegister_withValidRequest_shouldReturnCreated() throws Exception {
        // --- Arrange (Given) ---
        var validRequest = new RegisterRequestDto(
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        when(userService.registerCustomer(validRequest)).thenReturn(userDto);

        // --- Act & Assert (When & Then) ---
        mockMvc.perform(post("/api/users/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(content().json(objectMapper.writeValueAsString(userDto))); // Expect JSON response

        // Verify that the service method was called exactly once
        verify(userService, times(1)).registerCustomer(validRequest);
    }

    // Test Case 2: Registration request with invalid data (e.g., missing email)
    // This test implicitly checks your validation annotations (@NotBlank, @Email, etc.)
    @Test
    void whenPostRegister_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        // --- Arrange (Given) ---
        RegisterRequestDto invalidRequest = new RegisterRequestDto(
                "", // Invalid email
                "password123",
                "Test",
                "User"
        );

        // --- Act & Assert (When & Then) ---
        mockMvc.perform(post("/api/users/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request

        // Verify that the service method was never called
        verify(userService, times(0)).registerCustomer(invalidRequest);
    }

    // Test Case 3: Registration request with existing email
    @Test
    void whenPostRegister_withExistingEmail_shouldReturnConflict() throws Exception {
        // --- Arrange (Given) ---
        RegisterRequestDto existingEmailRequest = new RegisterRequestDto(
                "existing@example.com",
                "password123",
                "Test",
                "User"
        );
        when(userService.registerCustomer(existingEmailRequest)).thenThrow(new EmailExistsException());

        // --- Act & Assert (When & Then) ---
        mockMvc.perform(post("/api/users/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingEmailRequest)))
                .andExpect(status().isConflict()); // Expect HTTP 409 Conflict
    }

    // Test Case 4: Login request with valid credentials
    @Test
    @WithMockUser(username = "1")
    void whenPostLogin_withValidCredentials_shouldReturnOk() throws Exception {
        // --- Arrange (Given) ---
        LoginRequestDto validRequest = new LoginRequestDto(
                "test@example.com",
                "password123"
        );
        when(userService.loginUser(validRequest)).thenReturn(userDto);
        when(jwtService.generateToken(userDto.id().toString(), userDto.role().name())).thenReturn("token");

        // --- Act & Assert (When & Then) ---
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    // Test Case 5: Login request with invalid credentials
    @Test
    void whenPostLogin_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        // --- Arrange (Given) ---
        LoginRequestDto invalidRequest = new LoginRequestDto(
                "test@example.com",
                "wrong-password"
        );

        when(userService.loginUser(invalidRequest)).thenThrow(new BadCredentialsException(""));

        // --- Act & Assert (When & Then) ---
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized()); // Expect HTTP 401 Unauthorized
    }

    // Test Case 6: Get user profile with valid credentials
    @Test
    @WithMockUser(username = "1")
    void whenGetProfile_withValidCredentials_shouldReturnOk() throws Exception {
        // --- Arrange (Given) ---
        // --- Act & Assert (When & Then) ---
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @TestConfiguration
    static class TestConfig {
        // We are explicitly creating a bean of type UserService.
        // The Spring test context will use THIS bean instead of a real one.
        @Bean
        public UserService userService() {
            // Create a mock using Mockito's static mock() method
            return Mockito.mock(UserService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }
    }
}