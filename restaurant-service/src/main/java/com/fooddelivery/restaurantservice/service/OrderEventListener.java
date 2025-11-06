package com.fooddelivery.restaurantservice.service;

import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;
import com.fooddelivery.shared.event.OrderInTransitEvent;
import com.fooddelivery.shared.event.OrderDeliveredEvent;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private final RestaurantService restaurantService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final com.fooddelivery.restaurantservice.repository.RestaurantOrderRepository restaurantOrderRepository;

    @KafkaListener(topics = OrderPlacedEventPublisher.TOPIC, groupId = "restaurant-service-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received new order #{} for restaurant #{}", event.orderId(), event.restaurantId());

        // Persist this new order to the restaurant's local database
        restaurantService.createRestaurantOrder(event);

        Restaurant restaurant = restaurantService.getRestaurantById(event.restaurantId());
        String ownerId = String.valueOf(restaurant.getOwnerId());
        webSocketNotificationService.sendOrderStatusUpdateNotification(ownerId, OrderStatus.PENDING);
    }

    @KafkaListener(topics = OrderInTransitEventPublisher.TOPIC, groupId = "restaurant-service-group")
    public void handleOrderInTransitEvent(OrderInTransitEvent event) {
        log.info("Order #{} is now IN_TRANSIT (driver #{})", event.orderId(), event.driverId());

        // Update the order status in the restaurant's local database
        RestaurantOrder restaurantOrder = restaurantService.getRestaurantOrder(event.orderId());
        restaurantOrder.setLocalStatus(OrderStatus.IN_TRANSIT);
        restaurantOrderRepository.save(restaurantOrder);

        Restaurant restaurant = restaurantService.getRestaurantById(restaurantOrder.getRestaurantId());
        String ownerId = String.valueOf(restaurant.getOwnerId());
        webSocketNotificationService.sendOrderStatusUpdateNotification(ownerId, OrderStatus.IN_TRANSIT);
    }

    @KafkaListener(topics = OrderDeliveredEventPublisher.TOPIC, groupId = "restaurant-service-group")
    public void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
        log.info("Order #{} has been DELIVERED (driver #{})", event.orderId(), event.driverId());

        // Update the order status in the restaurant's local database
        RestaurantOrder restaurantOrder = restaurantService.getRestaurantOrder(event.orderId());
        restaurantOrder.setLocalStatus(OrderStatus.DELIVERED);
        restaurantOrderRepository.save(restaurantOrder);

        Restaurant restaurant = restaurantService.getRestaurantById(restaurantOrder.getRestaurantId());
        String ownerId = String.valueOf(restaurant.getOwnerId());
        webSocketNotificationService.sendOrderStatusUpdateNotification(ownerId, OrderStatus.DELIVERED);
    }
}
