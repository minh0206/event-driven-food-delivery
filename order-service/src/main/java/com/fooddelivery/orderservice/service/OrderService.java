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
import com.fooddelivery.shared.event.OrderItemDetails;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final List<OrderStatus> activeStatuses = List.of(
            OrderStatus.PENDING,
            OrderStatus.ACCEPTED);

    private final OrderRepository orderRepository;
    private final OrderPlacedEventPublisher orderPlacedEventPublisher;

    public Order getOrderById(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

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
        BigDecimal totalPrice = getTotalPrice(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        // --- KAFKA EVENT PUBLISHING ---
        // Create the event payload
        List<OrderItemDetails> itemDetails = savedOrder.getItems().stream()
                .map(item -> new OrderItemDetails(item.getId(), item.getMenuItemId(), item.getQuantity()))
                .toList();

        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getRestaurantId(),
                itemDetails);

        // Send to Kafka
        orderPlacedEventPublisher.publish(event);
        return savedOrder;
    }

    private BigDecimal getTotalPrice(List<OrderItem> orderItems) {
        // TODO: Fetch menu item details (especially price) from the restaurant-service
        // to prevent client-side tampering.

        return orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}