package com.fooddelivery.restaurantservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantOrderMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.service.RestaurantService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/restaurants/manage")
@AllArgsConstructor
public class RestaurantManagementController {
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;
    private final RestaurantService restaurantService;
    private final RestaurantOrderMapper restaurantOrderMapper;

    private Long getAuthenticatedUserId(Principal principal) {
        return Long.parseLong(principal.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public RestaurantDto updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto requestDto,
            @AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        return restaurantMapper.toDto(restaurantService.updateRestaurant(id, requestDto, ownerId));
    }

    @PostMapping("/{restaurantId}/menu")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<MenuItemDto> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemRequestDto requestDto,
            @AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        MenuItem newItem = restaurantService.addMenuItem(restaurantId, requestDto, ownerId);
        return new ResponseEntity<>(menuItemMapper.toDto(newItem), HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public MenuItemDto updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @RequestBody MenuItemRequestDto requestDto,
            @AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        MenuItem updatedItem = restaurantService.updateMenuItem(restaurantId, itemId, requestDto, ownerId);
        return menuItemMapper.toDto(updatedItem);
    }

    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        restaurantService.deleteMenuItem(restaurantId, itemId, ownerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public List<RestaurantOrderDto> getRestaurantOrders(@AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        List<RestaurantOrder> orders = restaurantService.getRestaurantOrders(ownerId);
        return orders.stream().map(restaurantOrderMapper::toDto).toList();
    }

    @PutMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public RestaurantOrderDto updateRestaurantOrder(
            @PathVariable Long orderId,
            @RequestBody RestaurantOrderRequestDto requestDto,
            @AuthenticationPrincipal Principal principal) {
        Long ownerId = getAuthenticatedUserId(principal);
        RestaurantOrder updatedOrder = restaurantService.updateRestaurantOrder(orderId, requestDto, ownerId);
        return restaurantOrderMapper.toDto(updatedOrder);
    }
}