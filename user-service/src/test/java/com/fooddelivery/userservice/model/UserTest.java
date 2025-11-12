package com.fooddelivery.userservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void userEntity_canBeCreatedWithAllFields() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.CUSTOMER);

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void userEntity_restaurantAdminHasRestaurantId() {
        User user = new User();
        user.setRole(Role.RESTAURANT_ADMIN);
        user.setRestaurantId(100L);

        assertEquals(Role.RESTAURANT_ADMIN, user.getRole());
        assertEquals(100L, user.getRestaurantId());
        assertNull(user.getDriverId());
    }

    @Test
    void userEntity_driverHasDriverId() {
        User user = new User();
        user.setRole(Role.DELIVERY_DRIVER);
        user.setDriverId(200L);

        assertEquals(Role.DELIVERY_DRIVER, user.getRole());
        assertEquals(200L, user.getDriverId());
        assertNull(user.getRestaurantId());
    }

    @Test
    void userEntity_customerHasNoSpecialIds() {
        User user = new User();
        user.setRole(Role.CUSTOMER);

        assertEquals(Role.CUSTOMER, user.getRole());
        assertNull(user.getRestaurantId());
        assertNull(user.getDriverId());
    }

    @Test
    void userEntity_toStringWorks() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setRole(Role.CUSTOMER);

        String toString = user.toString();
        assertNotNull(toString);
    }

    @Test
    void roleEnum_hasAllExpectedValues() {
        assertEquals("CUSTOMER", Role.CUSTOMER.name());
        assertEquals("RESTAURANT_ADMIN", Role.RESTAURANT_ADMIN.name());
        assertEquals("DELIVERY_DRIVER", Role.DELIVERY_DRIVER.name());
        assertEquals("SYSTEM_ADMIN", Role.SYSTEM_ADMIN.name());
    }
}
