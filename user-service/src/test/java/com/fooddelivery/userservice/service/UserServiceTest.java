package com.fooddelivery.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fooddelivery.shared.dto.RestaurantRequestDto;
import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.shared.feignclient.DeliveryServiceClient;
import com.fooddelivery.shared.feignclient.RestaurantServiceClient;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RestaurantServiceClient restaurantServiceClient;
    @Mock
    private DeliveryServiceClient deliveryServiceClient;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    private RegisterRequestDto buildRegisterReq() {
        return new RegisterRequestDto(
                "john@example.com",
                "password123",
                "John",
                "Doe",
                "",
                "",
                "");
    }

    private RegisterRequestDto buildRestaurantReq() {
        return new RegisterRequestDto(
                "owner@example.com",
                "password123",
                "Owner",
                "One",
                "Testaurant",
                "123 St",
                "Asian");
    }

    @Test
    void registerCustomer_savesWithCustomerRole() throws Exception {
        RegisterRequestDto req = buildRegisterReq();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        User saved = userService.registerCustomer(req);

        assertNotNull(saved.getId());
        assertEquals(Role.CUSTOMER, saved.getRole());
        assertEquals("ENC", saved.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerCustomer_throwsWhenEmailExists() {
        RegisterRequestDto req = buildRegisterReq();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailExistsException.class, () -> userService.registerCustomer(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRestaurantAdmin_callsRestaurantServiceAndSavesRestaurantId() throws Exception {
        RegisterRequestDto req = buildRestaurantReq();
        RestaurantRequestDto restaurantReq = new RestaurantRequestDto(
                req.restaurantName(),
                req.address(),
                req.cuisineType());

        when(userRepository.findByEmail(req.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(req.password())).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null)
                u.setId(20L);
            return u;
        });
        when(restaurantServiceClient.createRestaurant(restaurantReq)).thenReturn(Map.of("restaurantId", 77L));

        User saved = userService.registerRestaurantAdmin(req);

        assertEquals(77L, saved.getRestaurantId());
        assertEquals(Role.RESTAURANT_ADMIN, saved.getRole());
        assertEquals(20L, saved.getId());
        // Security context should be set with principal as user id
        assertTrue(
                SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals("20", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(restaurantServiceClient).createRestaurant(restaurantReq);
    }

    @Test
    void registerDriver_callsDeliveryServiceAndSavesDriverId() throws Exception {
        RegisterRequestDto req = buildRegisterReq();
        when(userRepository.findByEmail(req.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(req.password())).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null)
                u.setId(30L);
            return u;
        });
        when(deliveryServiceClient.createDriver()).thenReturn(Map.of("driverId", 88L));

        User saved = userService.registerDriver(req);

        assertEquals(88L, saved.getDriverId());
        assertEquals(Role.DELIVERY_DRIVER, saved.getRole());
        assertEquals(30L, saved.getId());
        assertTrue(
                SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals("30", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(deliveryServiceClient).createDriver();
    }

    @Test
    void loginUser_authenticatesAndReturnsUser() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(new User()));

        User u = userService.loginUser("a@b.com", "passw0rd");

        assertNotNull(u);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("a@b.com");
    }

    @Test
    void getUserById_returnsUser() {
        User user = new User();
        user.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(5L);

        assertEquals(5L, result.getId());
        verify(userRepository).findById(5L);
    }
}
