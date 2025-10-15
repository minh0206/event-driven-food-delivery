package com.fooddelivery.restaurantservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import com.fooddelivery.shared.config.SharedModuleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicRestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SharedModuleAutoConfiguration.class)
public class PublicRestaurantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Autowired
    private RestaurantService restaurantService;

    @Test
    void whenGetRestaurants_shouldReturnRestaurants() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @Test
    void whenGetRestaurantById_shouldReturnRestaurant() throws Exception {
        // Arrange
        long restaurantId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @Test
    void whenGetRestaurantMenu_shouldReturnMenuItems() throws Exception {
        // Arrange
        long restaurantId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/" + restaurantId + "/menu"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestaurantService restaurantService() {
            // Create a mock using Mockito's static mock() method
            return Mockito.mock(RestaurantService.class);
        }
    }
}
