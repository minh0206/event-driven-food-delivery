package com.fooddelivery.restaurantservice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderPlacedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class WebSocketNotificationService {
    private SimpMessagingTemplate messagingTemplate;

    public void sendNewOrderPlaced(String ownerId, OrderPlacedEvent event) {
        messagingTemplate.convertAndSendToUser(
                ownerId,
                "/queue/order-placed",
                event);
    }
}