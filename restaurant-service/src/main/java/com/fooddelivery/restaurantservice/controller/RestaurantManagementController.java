package com.fooddelivery.restaurantservice.controller;


import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/manage")
@AllArgsConstructor
public class RestaurantManagementController {
    private RestaurantService restaurantService;

    private Long getAuthenticatedUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantDto> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        RestaurantDto createdRestaurant = restaurantService.createRestaurant(requestDto, ownerId);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public RestaurantDto updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        return restaurantService.updateRestaurant(id, dto, ownerId);
    }

    @PostMapping("/{restaurantId}/menu")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<MenuItemDto> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        MenuItemDto newItem = restaurantService.addMenuItem(restaurantId, dto, ownerId);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public MenuItemDto updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @RequestBody MenuItemRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        return restaurantService.updateMenuItem(restaurantId, itemId, dto, ownerId);
    }

    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        restaurantService.deleteMenuItem(restaurantId, itemId, ownerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<Void> acceptOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        restaurantService.acceptOrder(orderId, ownerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<Void> rejectOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long ownerId = getAuthenticatedUserId(userDetails);
        restaurantService.rejectOrder(orderId, ownerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}