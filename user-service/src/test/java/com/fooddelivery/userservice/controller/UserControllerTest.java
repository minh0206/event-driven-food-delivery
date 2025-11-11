package com.fooddelivery.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.CustomerDto;
import com.fooddelivery.userservice.dto.DriverDto;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.RestaurantAdminDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerCustomer_returnsToken() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "john@example.com",
                "password123",
                "John",
                "Doe",
                "",
                "",
                "");

        User saved = new User();
        saved.setId(1L);
        saved.setRole(Role.CUSTOMER);

        when(userService.registerCustomer(any(RegisterRequestDto.class))).thenReturn(saved);
        when(jwtService.generateToken("1", Role.CUSTOMER.toString())).thenReturn("jwt-token");

        mockMvc.perform(post("/api/users/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "jwt-token"))));

        verify(userService).registerCustomer(any(RegisterRequestDto.class));
        verify(jwtService).generateToken("1", Role.CUSTOMER.toString());
    }

    @Test
    void registerRestaurantAdmin_returnsToken() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "owner@example.com",
                "password123",
                "Owner",
                "One",
                "Rest",
                "",
                "");

        User saved = new User();
        saved.setId(2L);
        saved.setRole(Role.RESTAURANT_ADMIN);

        when(userService.registerRestaurantAdmin(any(RegisterRequestDto.class))).thenReturn(saved);
        when(jwtService.generateToken("2", Role.RESTAURANT_ADMIN.toString())).thenReturn("jwt-token-2");

        mockMvc.perform(post("/api/users/register/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "jwt-token-2"))));

        verify(userService).registerRestaurantAdmin(any(RegisterRequestDto.class));
        verify(jwtService).generateToken("2", Role.RESTAURANT_ADMIN.toString());
    }

    @Test
    void registerDriver_returnsToken() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "driver@example.com",
                "password123",
                "Driver",
                "",
                "",
                "",
                "");

        User saved = new User();
        saved.setId(3L);
        saved.setRole(Role.DELIVERY_DRIVER);

        when(userService.registerDriver(any(RegisterRequestDto.class))).thenReturn(saved);
        when(jwtService.generateToken("3", Role.DELIVERY_DRIVER.toString())).thenReturn("jwt-token-3");

        mockMvc.perform(post("/api/users/register/driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "jwt-token-3"))));

        verify(userService).registerDriver(any(RegisterRequestDto.class));
        verify(jwtService).generateToken("3", Role.DELIVERY_DRIVER.toString());
    }

    @Test
    void login_returnsToken() throws Exception {
        LoginRequestDto req = new LoginRequestDto("a@b.com", "password123");

        User user = new User();
        user.setId(4L);
        user.setRole(Role.CUSTOMER);

        when(userService.loginUser("a@b.com", "password123")).thenReturn(user);
        when(jwtService.generateToken("4", Role.CUSTOMER.toString())).thenReturn("jwt-login");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "jwt-login"))));

        verify(userService).loginUser("a@b.com", "password123");
        verify(jwtService).generateToken("4", Role.CUSTOMER.toString());
    }

    @Test
    void getUserProfile_returnsRestaurantAdminDto_whenRoleRestaurantAdmin() throws Exception {
        Principal principal = () -> "10";
        User user = new User();
        user.setId(10L);
        user.setRole(Role.RESTAURANT_ADMIN);
        RestaurantAdminDto dto = new RestaurantAdminDto();

        when(userService.getUserById(10L)).thenReturn(user);
        when(userMapper.toRestaurantAdminDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/profile").principal(principal))
                .andExpect(status().isOk());

        verify(userMapper).toRestaurantAdminDto(user);
    }

    @Test
    void getUserProfile_returnsDriverDto_whenRoleDriver() throws Exception {
        Principal principal = () -> "11";
        User user = new User();
        user.setId(11L);
        user.setRole(Role.DELIVERY_DRIVER);
        DriverDto dto = new DriverDto();

        when(userService.getUserById(11L)).thenReturn(user);
        when(userMapper.toDriverDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/profile").principal(principal))
                .andExpect(status().isOk());

        verify(userMapper).toDriverDto(user);
    }

    @Test
    void getUserProfile_returnsCustomerDto_whenRoleCustomer() throws Exception {
        Principal principal = () -> "12";
        User user = new User();
        user.setId(12L);
        user.setRole(Role.CUSTOMER);
        CustomerDto dto = new CustomerDto();

        when(userService.getUserById(12L)).thenReturn(user);
        when(userMapper.toCustomerDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/profile").principal(principal))
                .andExpect(status().isOk());

        verify(userMapper).toCustomerDto(user);
    }

    @Test
    void registerCustomer_withInvalidEmail_returnsBadRequest() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "invalid-email",
                "password123",
                "John",
                "Doe",
                "",
                "",
                "");

        mockMvc.perform(post("/api/users/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withDifferentRoles_returnsCorrectToken() throws Exception {
        LoginRequestDto req = new LoginRequestDto("driver@example.com", "password123");

        User driver = new User();
        driver.setId(5L);
        driver.setRole(Role.DELIVERY_DRIVER);

        when(userService.loginUser("driver@example.com", "password123")).thenReturn(driver);
        when(jwtService.generateToken("5", Role.DELIVERY_DRIVER.toString())).thenReturn("driver-token");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "driver-token"))));

        verify(jwtService).generateToken("5", Role.DELIVERY_DRIVER.toString());
    }

    @Test
    void getUserProfile_withSystemAdmin_returnsCustomerDto() throws Exception {
        Principal principal = () -> "99";
        User admin = new User();
        admin.setId(99L);
        admin.setRole(Role.SYSTEM_ADMIN);
        CustomerDto dto = new CustomerDto();

        when(userService.getUserById(99L)).thenReturn(admin);
        when(userMapper.toCustomerDto(admin)).thenReturn(dto);

        mockMvc.perform(get("/api/users/profile").principal(principal))
                .andExpect(status().isOk());

        verify(userMapper).toCustomerDto(admin);
    }

    @Test
    void registerRestaurantAdmin_withCompleteData_returnsToken() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "complete@restaurant.com",
                "securePass123",
                "Restaurant",
                "Owner",
                "Best Restaurant",
                "456 Main St",
                "Italian");

        User saved = new User();
        saved.setId(7L);
        saved.setRole(Role.RESTAURANT_ADMIN);
        saved.setRestaurantId(100L);

        when(userService.registerRestaurantAdmin(any(RegisterRequestDto.class))).thenReturn(saved);
        when(jwtService.generateToken("7", Role.RESTAURANT_ADMIN.toString())).thenReturn("restaurant-token");

        mockMvc.perform(post("/api/users/register/restaurant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "restaurant-token"))));

        verify(userService).registerRestaurantAdmin(any(RegisterRequestDto.class));
    }

    @Test
    void registerDriver_withMinimalData_returnsToken() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "minimal@driver.com",
                "password123",
                "Min",
                "",
                "",
                "",
                "");

        User saved = new User();
        saved.setId(8L);
        saved.setRole(Role.DELIVERY_DRIVER);
        saved.setDriverId(200L);

        when(userService.registerDriver(any(RegisterRequestDto.class))).thenReturn(saved);
        when(jwtService.generateToken("8", Role.DELIVERY_DRIVER.toString())).thenReturn("minimal-driver-token");

        mockMvc.perform(post("/api/users/register/driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("token", "minimal-driver-token"))));

        verify(userService).registerDriver(any(RegisterRequestDto.class));
    }
}
