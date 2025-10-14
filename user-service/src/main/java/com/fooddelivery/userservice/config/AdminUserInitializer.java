package com.fooddelivery.userservice.config;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            System.out.println("Created initial SYSTEM_ADMIN user.");
        }
    }
}