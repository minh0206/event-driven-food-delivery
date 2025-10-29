package com.fooddelivery.restaurantservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderPlacedEvent;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNewOrderPlaced(String ownerId, OrderPlacedEvent event) {
        messagingTemplate.convertAndSendToUser(
                ownerId,
                "/queue/order-placed",
                event);
    }
}