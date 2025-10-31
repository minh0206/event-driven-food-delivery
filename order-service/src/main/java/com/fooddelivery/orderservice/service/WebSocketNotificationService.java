package com.fooddelivery.orderservice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.dto.DriverLocationDto;
import com.fooddelivery.orderservice.dto.OrderStatusUpdateDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderStatusUpdate(String userId, OrderStatusUpdateDto statusUpdateDto) {
        // The destination is "/queue/order-updates". The "/user" prefix is handled by
        // Spring
        // to ensure this message is routed only to the specified user's session.
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/order-updates",
                statusUpdateDto);
    }

    public void sendDriverLocation(String userId, DriverLocationDto locationDto) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/driver-location",
                locationDto);
    }
}