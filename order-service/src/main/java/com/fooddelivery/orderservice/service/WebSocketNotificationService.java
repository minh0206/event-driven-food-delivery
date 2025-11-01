package com.fooddelivery.orderservice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.dto.DriverLocationDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderUpdate(String userId) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/order-updates",
                "Order updated");
    }

    public void sendDriverLocation(String userId, DriverLocationDto locationDto) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/driver-location",
                locationDto);
    }
}