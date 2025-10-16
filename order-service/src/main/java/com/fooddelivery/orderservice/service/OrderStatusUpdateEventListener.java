package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.OrderStatusUpdateDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.event.OrderStatusUpdateEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderStatusUpdateEventListener {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "order_status_updates", groupId = "order-service-group")
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        log.info("Updating status for order #{} to {}", event.orderId(), event.newStatus());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        // 1. Update the database
        order.setStatus(OrderStatus.valueOf(event.newStatus()));
        orderRepository.save(order);

        // 2. Create the WebSocket DTO
        OrderStatusUpdateDto wsUpdate = new OrderStatusUpdateDto(
                order.getId(),
                order.getStatus().name(),
                "Your order has been " + order.getStatus().name().toLowerCase()
        );

        // 3. Push the update via WebSocket to the specific customer
        // We convert the customerId (Long) to a String because the Principal's name is a String.
        String customerId = String.valueOf(order.getCustomerId());
        webSocketNotificationService.sendOrderStatusUpdate(customerId, wsUpdate);
    }
}
