package com.fooddelivery.restaurantservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.shared.event.OrderPlacedEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private final RestaurantService restaurantService;
    private final WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = "order_placed", groupId = "restaurant-service-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received new order #{} for restaurant #{}", event.orderId(), event.restaurantId());

        // Persist this new order to the restaurant's local database
        restaurantService.createRestaurantOrder(event);

        // **CRITICAL STEP**: Find the restaurant to get the owner's user ID.
        Restaurant restaurant = restaurantService.getRestaurantById(event.restaurantId());
        String ownerId = String.valueOf(restaurant.getOwnerId());

        // Push the real-time notification
        webSocketNotificationService.sendNewOrderPlaced(ownerId, event);
    }
}
