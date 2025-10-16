package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.DriverLocationDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service

public class DriverLocationUpdateEventListener {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "driver_location_updates", groupId = "order-service-group")
    public void handleDriverLocationUpdate(DriverLocationUpdateEvent event) {
        Order order = orderRepository.findById(event.orderId()).orElse(null);
        if (order == null) return; // Ignore if order isn't found

        // Prepare payload for the client
        DriverLocationDto locationDto = new DriverLocationDto(event.orderId(), event.latitude(), event.longitude());

        // Send to the specific customer's WebSocket session
        // on a new, dedicated destination for location data.
        webSocketNotificationService.sendDriverLocationUpdate(
                String.valueOf(order.getCustomerId()),
                locationDto
        );
    }
}
