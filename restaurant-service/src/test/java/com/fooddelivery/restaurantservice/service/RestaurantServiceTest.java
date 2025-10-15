package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    // Create restaurant
    @Test
    void whenCreateRestaurant_withValidUser_shouldCreateRestaurant() {
        // Arrange
        Long ownerId = 1L;
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                "Test Address",
                "Test Cuisine");
        when(restaurantRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        // Act
        RestaurantDto result = restaurantService.createRestaurant(requestDto, ownerId);

        // Assert
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void whenCreateRestaurant_withExistingRestaurant_shouldThrowException() {
        // Arrange
        Long ownerId = 1L;
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                "Test Address",
                "Test Cuisine");
        when(restaurantRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(new Restaurant()));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            restaurantService.createRestaurant(requestDto, ownerId);
        });

        verify(restaurantRepository, times(0)).save(any(Restaurant.class));
    }

    // Update restaurant
    @Test
    void whenUpdateRestaurant_withUserIsOwner_shouldUpdateRestaurant() {
        // Arrange
        Long restaurantId = 1L;
        Long ownerId = 100L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Test Restaurant",
                "Test Address",
                "Test Cuisine");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act
        RestaurantDto result = restaurantService.updateRestaurant(restaurantId, requestDto, ownerId);

        // Assert
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void whenUpdateRestaurant_withUserIsNotOwner_shouldUpdateRestaurant() {
        // Arrange
        Long restaurantId = 1L;
        Long correctOwnerId = 100L;
        Long wrongOwnerId = 200L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(correctOwnerId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        RestaurantRequestDto dto = new RestaurantRequestDto("New Name", "New Address", "New Cuisine");

        // Act & Assert
        // Verify that a SecurityException is thrown when the wrong owner tries to update
        assertThrows(SecurityException.class, () -> {
            restaurantService.updateRestaurant(restaurantId, dto, wrongOwnerId);
        });

        // Verify that save is never called
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    // Add menu item
    @Test
    void whenAddMenuItem_withValidOwner_shouldAddMenuItem() {
        // Arrange
        Long restaurantId = 1L;
        Long ownerId = 100L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test Item",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act
        restaurantService.addMenuItem(restaurantId, requestDto, ownerId);

        // Assert
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void whenAddMenuItem_withInvalidOwner_shouldThrowException() {
        // Arrange
        Long restaurantId = 1L;
        Long ownerId = 100L;
        Long wrongOwnerId = 200L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test Item",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        // Verify that a SecurityException is thrown when the wrong owner tries to update
        assertThrows(SecurityException.class, () -> {
            restaurantService.addMenuItem(restaurantId, requestDto, wrongOwnerId);
        });

        // Verify that save is never called
        verify(menuItemRepository, times(0)).save(any(MenuItem.class));
    }

    // Update menu item
    @Test
    void whenUpdateMenuItem_withValidOwner_shouldUpdateMenuItem() {
        // Arrange
        Long restaurantId = 1L;
        Long itemId = 1L;
        Long ownerId = 100L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setRestaurant(restaurant);

        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test Item",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));

        // Act
        restaurantService.updateMenuItem(restaurantId, itemId, requestDto, ownerId);

        // Assert
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void whenUpdateMenuItem_withInvalidOwner_shouldThrowException() {
        // Arrange
        Long restaurantId = 1L;
        Long itemId = 1L;
        Long ownerId = 100L;
        Long wrongOwnerId = 200L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        MenuItemRequestDto requestDto = new MenuItemRequestDto(
                "Test Item",
                "Test Description",
                BigDecimal.ONE);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        // Verify that a SecurityException is thrown when the wrong owner tries to update
        assertThrows(SecurityException.class, () -> {
            restaurantService.updateMenuItem(restaurantId, itemId, requestDto, wrongOwnerId);
        });

        // Verify that save is never called
        verify(menuItemRepository, times(0)).save(any(MenuItem.class));
    }

    // Delete menu item
    @Test
    void whenDeleteMenuItem_withValidOwner_shouldDeleteMenuItem() {
        // Arrange
        Long restaurantId = 1L;
        Long itemId = 1L;
        Long ownerId = 100L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setRestaurant(restaurant);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));

        // Act
        restaurantService.deleteMenuItem(restaurantId, itemId, ownerId);

        // Assert
        verify(menuItemRepository, times(1)).delete(menuItem);
    }

    @Test
    void whenDeleteMenuItem_withInvalidOwner_shouldThrowException() {
        // Arrange
        Long restaurantId = 1L;
        Long itemId = 1L;
        Long ownerId = 100L;
        Long wrongOwnerId = 200L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        // Verify that a SecurityException is thrown when the wrong owner tries to delete
        assertThrows(SecurityException.class, () -> {
            restaurantService.deleteMenuItem(restaurantId, itemId, wrongOwnerId);
        });

        // Verify that delete is never called
        verify(menuItemRepository, times(0)).delete(any(MenuItem.class));
    }

    // Get all restaurants
    @Test
    void whenGetAllRestaurants_shouldReturnAllRestaurants() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurants = new PageImpl<>(List.of(new Restaurant()));

        when(restaurantRepository.findAll(pageable)).thenReturn(restaurants);

        // Act
        Page<RestaurantDto> result = restaurantService.getAllRestaurants(pageable);

        // Assert
        assertNotNull(result);
    }

    // Get restaurant by id
    @Test
    void whenGetRestaurantById_shouldReturnRestaurant() {
        // Arrange
        Long id = 1L;
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act
        RestaurantDto result = restaurantService.getRestaurantById(id);

        // Assert
        verify(restaurantRepository, times(1)).findById(id);
    }

    // Get restaurant menu
    @Test
    void whenGetRestaurantMenu_shouldReturnMenu() {
        // Arrange
        Long id = 1L;
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act
        List<MenuItemDto> result = restaurantService.getRestaurantMenu(id);

        // Assert
        verify(restaurantRepository, times(1)).findById(id);
    }
}