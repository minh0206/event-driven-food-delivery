package com.fooddelivery.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.service.OrderService;
import com.fooddelivery.shared.dto.DriverOrderDto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/internal/orders")
@AllArgsConstructor
public class InternalOrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/{orderId}")
    public DriverOrderDto getDriverOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return orderMapper.toDriverOrderDto(order);
    }

}
