package com.fooddelivery.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.dto.OrderDto;
import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.service.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private OrderMapper orderMapper;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody @Valid CreateOrderRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long customerId = Long.parseLong(userDetails.getUsername());
        Order createdOrder = orderService.createOrder(requestDto, customerId);
        return new ResponseEntity<>(orderMapper.toDto(createdOrder), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderDto> getOrdersByUserId(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Order> orders = orderService.getOrdersByCustomerId(userId);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    @GetMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public List<OrderDto> getOrdersByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "true") boolean isActive) {
        List<Order> orders = isActive ? orderService.getActiveOrdersByRestaurantId(restaurantId)
                : orderService.getHistoricalOrdersByRestaurantId(restaurantId);
        return orders.stream().map(orderMapper::toDto).toList();
    }
}