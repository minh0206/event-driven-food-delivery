package com.fooddelivery.restaurantservice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.enumerate.OrderStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class WebSocketNotificationService {
    private SimpMessagingTemplate messagingTemplate;

    public void sendOrderStatusUpdateNotification(String userId, OrderStatus status) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/order-updates",
                status);
    }
}