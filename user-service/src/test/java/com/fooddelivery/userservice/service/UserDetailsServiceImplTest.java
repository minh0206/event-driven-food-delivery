package com.fooddelivery.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_returnsUserDetails_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));

        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}
