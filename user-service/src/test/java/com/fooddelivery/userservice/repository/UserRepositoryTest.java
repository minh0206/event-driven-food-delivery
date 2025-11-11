package com.fooddelivery.userservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.User;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_returnsUser_whenEmailExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.CUSTOMER);

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test", found.get().getFirstName());
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void save_persistsUser_withAllFields() {
        User user = new User();
        user.setEmail("driver@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Driver");
        user.setRole(Role.DELIVERY_DRIVER);
        user.setDriverId(123L);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("driver@example.com", saved.getEmail());
        assertEquals(Role.DELIVERY_DRIVER, saved.getRole());
        assertEquals(123L, saved.getDriverId());
    }

    @Test
    void save_persistsRestaurantAdmin_withRestaurantId() {
        User user = new User();
        user.setEmail("admin@restaurant.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Restaurant");
        user.setLastName("Admin");
        user.setRole(Role.RESTAURANT_ADMIN);
        user.setRestaurantId(456L);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals(Role.RESTAURANT_ADMIN, saved.getRole());
        assertEquals(456L, saved.getRestaurantId());
    }

    @Test
    void findById_returnsUser_whenIdExists() {
        User user = new User();
        user.setEmail("findme@example.com");
        user.setPassword("password");
        user.setFirstName("Find");
        user.setLastName("Me");
        user.setRole(Role.CUSTOMER);

        User saved = entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("findme@example.com", found.get().getEmail());
    }

    @Test
    void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<User> found = userRepository.findById(99999L);

        assertFalse(found.isPresent());
    }
}
