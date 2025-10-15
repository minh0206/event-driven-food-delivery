package com.fooddelivery.restaurantservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SharedModuleAutoConfiguration.class)
public class RestaurantManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Autowired
    private RestaurantService restaurantService;

    @Test
    @WithMockUser(username = "1")
    void whenCreateRestaurant_shouldCreateRestaurant() throws Exception {
        // Arrange
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                "Test Address",
                "Test Cuisine");

        when(restaurantService.createRestaurant(requestDto, 1L)).thenReturn(
                new RestaurantDto(1L, requestDto.name(), requestDto.address(), requestDto.cuisineType(), 1L)
        );

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/manage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // Expect HTTP 201 Created
    }

    @Test
    @WithMockUser(username = "1")
    void whenUpdateRestaurant_shouldUpdateRestaurant() throws Exception {
        // Arrange
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                "Test Address",
                "Test Cuisine");

        when(restaurantService.updateRestaurant(1L, requestDto, 1L)).thenReturn(
                new RestaurantDto(1L, requestDto.name(), requestDto.address(), requestDto.cuisineType(), 1L)
        );

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/manage/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @Test
    @WithMockUser(username = "1")
    void whenAddMenuItem_shouldAddMenuItem() throws Exception {
        // Arrange
        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test MenuItem",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantService.addMenuItem(1L, requestDto, 1L)).thenReturn(
                new MenuItemDto(1L, requestDto.name(), requestDto.description(), requestDto.price(), 1L)
        );

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/manage/1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // Expect HTTP 201 Created
    }

    @Test
    @WithMockUser(username = "1")
    void whenUpdateMenuItem_shouldUpdateMenuItem() throws Exception {
        // Arrange
        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test MenuItem",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantService.updateMenuItem(1L, 1L, requestDto, 1L)).thenReturn(
                new MenuItemDto(1L, requestDto.name(), requestDto.description(), requestDto.price(), 1L)
        );

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/manage/1/menu/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @Test
    @WithMockUser(username = "1")
    void whenDeleteMenuItem_shouldDeleteMenuItem() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/manage/1/menu/1"))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
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
