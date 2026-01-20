package com.fooddelivery.userservice.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private AdminUserInitializer adminUserInitializer;

    private static final String TEST_ADMIN_EMAIL = "test-admin@foodapp.com";
    private static final String TEST_ADMIN_PASSWORD = "test_admin_password";
    private static final String ENCODED_PASSWORD = "ENCODED_ADMIN_PASS";

    @BeforeEach
    void setUp() {
        adminUserInitializer = new AdminUserInitializer(passwordEncoder, userRepository);
        // Set the @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(adminUserInitializer, "adminEmail", TEST_ADMIN_EMAIL);
        ReflectionTestUtils.setField(adminUserInitializer, "adminPassword", TEST_ADMIN_PASSWORD);
    }

    @Test
    void run_createsAdminUser_whenAdminDoesNotExist() throws Exception {
        when(userRepository.findByEmail(TEST_ADMIN_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        adminUserInitializer.run();

        verify(userRepository).findByEmail(TEST_ADMIN_EMAIL);
        verify(passwordEncoder).encode(TEST_ADMIN_PASSWORD);
        verify(userRepository).save(argThat(user -> user.getEmail().equals(TEST_ADMIN_EMAIL) &&
                user.getPassword().equals(ENCODED_PASSWORD) &&
                user.getFirstName().equals("System") &&
                user.getLastName().equals("Admin") &&
                user.getRole() == Role.SYSTEM_ADMIN));
    }

    @Test
    void run_doesNotCreateAdminUser_whenAdminAlreadyExists() throws Exception {
        User existingAdmin = new User();
        existingAdmin.setEmail(TEST_ADMIN_EMAIL);
        existingAdmin.setRole(Role.SYSTEM_ADMIN);

        when(userRepository.findByEmail(TEST_ADMIN_EMAIL)).thenReturn(Optional.of(existingAdmin));

        adminUserInitializer.run();

        verify(userRepository).findByEmail(TEST_ADMIN_EMAIL);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void run_withArguments_stillCreatesAdmin() throws Exception {
        when(userRepository.findByEmail(TEST_ADMIN_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        adminUserInitializer.run("arg1", "arg2");

        verify(userRepository).save(any(User.class));
    }
}
