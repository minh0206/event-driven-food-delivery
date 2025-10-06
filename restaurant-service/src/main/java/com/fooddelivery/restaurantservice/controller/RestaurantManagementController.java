package com.fooddelivery.restaurantservice.controller;


import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/restaurants/manage")
@AllArgsConstructor
public class RestaurantManagementController {
    private RestaurantService restaurantService;

    private Long getAuthenticatedUserId(Principal principal) {
        // In a real app, you'd parse the JWT principal to get the user ID
        // For now, we'll simulate it.
        return Long.parseLong(principal.getName());
    }

    @PostMapping
    // @PreAuthorize("hasRole('RESTAURANT_ADMIN')") -> Add this when security is fully configured
    public ResponseEntity<RestaurantDto> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        RestaurantDto createdRestaurant = restaurantService.createRestaurant(requestDto, ownerId);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public RestaurantDto updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto dto,
            Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        return restaurantService.updateRestaurant(id, dto, ownerId);
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemDto> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemRequestDto dto,
            Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        MenuItemDto newItem = restaurantService.addMenuItem(restaurantId, dto, ownerId);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    // TODO: Add @PutMapping("/{restaurantId}/menu/{itemId}") and @DeleteMapping("/{restaurantId}/menu/{itemId}")
    @PutMapping("/{restaurantId}/menu/{itemId}")
    public MenuItemDto updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @RequestBody MenuItemRequestDto dto,
            Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        return restaurantService.updateMenuItem(restaurantId, itemId, dto, ownerId);
    }

    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        restaurantService.deleteMenuItem(restaurantId, itemId, ownerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}