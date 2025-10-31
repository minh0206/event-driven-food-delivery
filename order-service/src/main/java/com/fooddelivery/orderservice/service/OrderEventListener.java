package com.fooddelivery.orderservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.dto.OrderStatusUpdateDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.event.OrderStatusUpdateEvent;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "order_status_updates", groupId = "order-service-group")
    public void handleOrderStatusUpdateEvent(OrderStatusUpdateEvent event) {
        log.info("Updating status for order #{} to {}", event.orderId(), event.newStatus());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        // 1. Update the database
        order.setStatus(event.newStatus());
        orderRepository.save(order);

        // 2. Create the WebSocket DTO
        OrderStatusUpdateDto wsUpdate = new OrderStatusUpdateDto(
                order.getId(),
                order.getStatus());

        // 3. Push the update via WebSocket to the specific customer
        // We convert the customerId (Long) to a String because the Principal's name is
        // a String.
        String customerId = String.valueOf(order.getCustomerId());
        webSocketNotificationService.sendOrderStatusUpdate(customerId, wsUpdate);
    }
}
