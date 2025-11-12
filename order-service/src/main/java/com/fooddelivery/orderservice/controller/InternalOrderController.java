package com.fooddelivery.orderservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.service.OrderService;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.dto.MasterOrderDto;
import com.fooddelivery.shared.enumerate.OrderStatus;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/internal/orders")
@AllArgsConstructor
public class InternalOrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/restaurant/{restaurantId}")
    // @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public Page<MasterOrderDto> getHistoryOrdersByRestaurantId(
            @PathVariable Long restaurantId,
            Pageable pageable) {
        Page<Order> orders = orderService.getOrdersByRestaurantId(restaurantId, OrderStatus.DELIVERED, pageable);
        return orders.map(orderMapper::toMasterOrderDto);
    }

    @GetMapping("/driver/{orderId}")
    // @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public DriverOrderDto getDriverOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return orderMapper.toDriverOrderDto(order);
    }
}
