package com.fooddelivery.restaurantservice.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.service.RestaurantService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/internal/restaurants")
public class InternalRestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping()
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public Map<String, Long> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            Principal principal) {
        Long ownerId = Long.parseLong(principal.getName());
        Restaurant createdRestaurant = restaurantService.createRestaurant(requestDto, ownerId);
        return Map.of("restaurantId", createdRestaurant.getId());
    }
}
