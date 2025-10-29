package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.shared.event.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventListener {
    // You might want to save the incoming order to the restaurant's DB
    // so the restaurant owner can see it on their dashboard.

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "order_placed", groupId = "restaurant-service-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received new order #{} for restaurant #{}", event.orderId(), event.restaurantId());

        // TODO: Persist this new order notification to the restaurant's local database
        // so it can be displayed on the restaurant's order management dashboard.

        // **CRITICAL STEP**: Find the restaurant to get the owner's user ID.
        restaurantRepository.findById(event.restaurantId()).ifPresent(restaurant -> {
            String ownerId = String.valueOf(restaurant.getOwnerId());

            // Push the real-time notification
            webSocketNotificationService.sendNewOrderPlaced(ownerId, event);
        });
    }
}