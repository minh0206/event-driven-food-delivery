package com.fooddelivery.restaurantservice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.service.RestaurantService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/restaurants")
public class PublicRestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;

    @GetMapping
    public Page<RestaurantDto> getAllRestaurants(Pageable pageable) {
        // Pageable is automatically resolved by Spring from query params
        // (?page=0&size=10)
        return restaurantService.getAllRestaurants(pageable).map(restaurantMapper::toDto);
    }

    @GetMapping("/{id}")
    public RestaurantDto getRestaurantById(@PathVariable Long id) {
        return restaurantMapper.toDto(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/{id}/menu")
    public List<MenuItemDto> getRestaurantMenu(@PathVariable Long id) {
        List<MenuItem> menu = restaurantService.getRestaurantMenu(id);
        return menu.stream().map(menuItemMapper::toDto).toList();
    }
}