package com.fooddelivery.userservice.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserInitializer adminUserInitializer;

    @Test
    void run_createsAdminUser_whenAdminDoesNotExist() throws Exception {
        when(userRepository.findByEmail("admin@foodapp.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin_password_123")).thenReturn("ENCODED_ADMIN_PASS");

        adminUserInitializer.run();

        verify(userRepository).findByEmail("admin@foodapp.com");
        verify(passwordEncoder).encode("admin_password_123");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("admin@foodapp.com") &&
                        user.getPassword().equals("ENCODED_ADMIN_PASS") &&
                        user.getFirstName().equals("System") &&
                        user.getLastName().equals("Admin") &&
                        user.getRole() == Role.SYSTEM_ADMIN
        ));
    }

    @Test
    void run_doesNotCreateAdminUser_whenAdminAlreadyExists() throws Exception {
        User existingAdmin = new User();
        existingAdmin.setEmail("admin@foodapp.com");
        existingAdmin.setRole(Role.SYSTEM_ADMIN);

        when(userRepository.findByEmail("admin@foodapp.com")).thenReturn(Optional.of(existingAdmin));

        adminUserInitializer.run();

        verify(userRepository).findByEmail("admin@foodapp.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void run_withArguments_stillCreatesAdmin() throws Exception {
        when(userRepository.findByEmail("admin@foodapp.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin_password_123")).thenReturn("ENCODED_ADMIN_PASS");

        adminUserInitializer.run("arg1", "arg2");

        verify(userRepository).save(any(User.class));
    }
}
