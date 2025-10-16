package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.shared.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j // For logging
public class OrderEventListener {
    // You might want to save the incoming order to the restaurant's DB
    // so the restaurant owner can see it on their dashboard.

    @KafkaListener(topics = "order_placed", groupId = "restaurant-service-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received new order #{} for restaurant #{}", event.orderId(), event.restaurantId());
        // TODO: Persist this new order notification to the restaurant's local database
        // so it can be displayed on the restaurant's order management dashboard.
    }
}