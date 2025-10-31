package com.fooddelivery.orderservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {
    private final List<OrderStatus> activeStatuses = List.of(
            OrderStatus.PENDING,
            OrderStatus.ACCEPTED,
            OrderStatus.PREPARING);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Order> getActiveOrdersByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantIdAndStatusIn(restaurantId, activeStatuses);
    }

    public List<Order> getHistoricalOrdersByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantIdAndStatusNotIn(restaurantId, activeStatuses);
    }

    @Transactional // Ensures the whole operation is a single transaction
    public Order createOrder(CreateOrderRequestDto requestDto, Long customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setRestaurantId(requestDto.restaurantId());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = requestDto.items().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            item.setMenuItemId(itemDto.menuItemId());
            item.setQuantity(itemDto.quantity());
            item.setPrice(itemDto.price());
            item.setOrder(order); // Link back to the order
            return item;
        }).toList();
        order.setItems(orderItems);

        // Calculate total price
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        orderEventPublisher.publishOrderPlacedEvent(savedOrder);

        return savedOrder;
    }
}