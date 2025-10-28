package com.fooddelivery.orderservice.controller;

import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody @Valid CreateOrderRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.parseLong(userDetails.getUsername());
        Order newOrder = orderService.createOrder(requestDto, customerId);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Order> getOrdersByUserId(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return orderService.getOrdersByCustomerId(userId);
    }

    @GetMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public List<Order> getOrdersByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "true") boolean isActive) {
        if (isActive)
            return orderService.getActiveOrdersByRestaurantId(restaurantId);

        return orderService.getHistoricalOrdersByRestaurantId(restaurantId);
    }
}