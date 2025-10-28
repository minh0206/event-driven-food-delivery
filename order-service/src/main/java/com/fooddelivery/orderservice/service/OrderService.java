package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.orderservice.model.OrderStatus;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.dto.OrderItemDto;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final String TOPIC_ORDER_PLACED = "order_placed";
    private final List<OrderStatus> activeStatuses = List.of(
            OrderStatus.PENDING,
            OrderStatus.ACCEPTED,
            OrderStatus.PREPARING);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

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
        }).collect(Collectors.toList());
        order.setItems(orderItems);

        // Calculate total price
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        // --- KAFKA EVENT PUBLISHING ---
        // Create the event payload
        List<OrderItemDto> itemDetails = savedOrder.getItems().stream()
                .map(item -> new OrderItemDto(item.getMenuItemId(), item.getQuantity()))
                .collect(Collectors.toList());

        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getRestaurantId(),
                savedOrder.getCustomerId(),
                itemDetails);

        // Send to Kafka
        kafkaTemplate.send(TOPIC_ORDER_PLACED, event);

        return savedOrder;
    }
}