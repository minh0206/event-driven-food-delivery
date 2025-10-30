package com.fooddelivery.userservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {
    @Lazy
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (userRepository.findByEmail("admin@foodapp.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@foodapp.com");
            admin.setPassword(passwordEncoder.encode("admin_password_123")); // Use a strong, configured password
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRole(Role.SYSTEM_ADMIN);

            userRepository.save(admin);
            log.info("Created initial SYSTEM_ADMIN user.");
        }
    }
}