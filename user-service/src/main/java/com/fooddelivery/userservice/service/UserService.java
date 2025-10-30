package com.fooddelivery.userservice.service;

import com.fooddelivery.shared.exception.EmailExistsException;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User registerNewUser(User user, Role role) throws EmailExistsException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistsException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        user.setRole(role); // Set role

        return userRepository.save(user);
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
