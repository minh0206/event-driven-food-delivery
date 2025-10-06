package com.fooddelivery.restaurantservice.controller;


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
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody RestaurantRequestDto requestDto, Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        RestaurantDto createdRestaurant = restaurantService.createRestaurant(requestDto, ownerId);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public RestaurantDto updateRestaurant(@PathVariable Long id, @RequestBody RestaurantRequestDto dto, Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        return restaurantService.updateRestaurant(id, dto, ownerId);
    }
}