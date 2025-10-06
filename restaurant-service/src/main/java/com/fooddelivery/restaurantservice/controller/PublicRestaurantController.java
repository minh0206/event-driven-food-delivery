package com.fooddelivery.restaurantservice.controller;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/restaurants")
public class PublicRestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public Page<RestaurantDto> getAllRestaurants(Pageable pageable) {
        return restaurantService.getAllRestaurants(pageable);
    }

    @GetMapping("/{id}")
    public RestaurantDto getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @GetMapping("/{id}/menu")
    public List<MenuItemDto> getRestaurantMenu(@PathVariable Long id) {
        return restaurantService.getRestaurantMenu(id);
    }
}