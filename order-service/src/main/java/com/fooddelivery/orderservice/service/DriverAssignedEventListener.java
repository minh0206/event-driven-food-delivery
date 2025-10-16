package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.OrderStatusUpdateDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.event.DriverAssignedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverAssignedEventListener {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "driver_assigned", groupId = "order-service-group")
    public void handleDriverAssigned(DriverAssignedEvent event) {
        log.info("Driver assigned to order #{}. Updating status.", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + event.orderId()));

        // Add a new status to track this stage
        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        orderRepository.save(order);

        // Notify the customer via WebSocket
        OrderStatusUpdateDto wsUpdate = new OrderStatusUpdateDto(
                order.getId(),
                order.getStatus().name(),
                "A driver is on the way to the restaurant!"
        );
        String customerId = String.valueOf(order.getCustomerId());
        webSocketNotificationService.sendOrderStatusUpdate(customerId, wsUpdate);
    }
}
