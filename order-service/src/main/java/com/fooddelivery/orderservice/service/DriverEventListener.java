package com.fooddelivery.orderservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.dto.DriverLocationDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class DriverEventListener {
    private final OrderRepository orderRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "driver_assigned", groupId = "order-service-group")
    public void handleDriverAssignedEvent(DriverAssignedEvent event) {
        log.info("Driver assigned to order #{}. Updating status.", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + event.orderId()));

        // Add a new status to track this stage
        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        orderRepository.save(order);

        // Notify the customer via WebSocket
        String customerId = String.valueOf(order.getCustomerId());
        webSocketNotificationService.sendOrderUpdate(customerId);
    }

    @KafkaListener(topics = "driver_location_updates", groupId = "order-service-group")
    public void handleDriverLocationUpdateEvent(DriverLocationUpdateEvent event) {
        Order order = orderRepository.findById(event.orderId()).orElse(null);
        if (order == null)
            return; // Ignore if order isn't found

        // Prepare payload for the client
        DriverLocationDto location = new DriverLocationDto(event.orderId(), event.latitude(), event.longitude());

        // Send to the specific customer's WebSocket session
        // on a new, dedicated destination for location data.
        webSocketNotificationService.sendDriverLocation(
                String.valueOf(order.getCustomerId()),
                location);
    }
}
