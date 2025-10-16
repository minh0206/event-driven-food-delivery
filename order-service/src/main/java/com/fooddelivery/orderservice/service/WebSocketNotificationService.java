package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.DriverLocationDto;
import com.fooddelivery.orderservice.dto.OrderStatusUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Sends an order status update to a specific user.
     *
     * @param userId The ID of the user to notify. Must match the user's Principal name.
     * @param update The update payload to send.
     */
    public void sendOrderStatusUpdate(String userId, OrderStatusUpdateDto update) {
        // The destination is "/queue/order-updates". The "/user" prefix is handled by Spring
        // to ensure this message is routed only to the specified user's session.
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/order-updates",
                update
        );
    }

    public void sendDriverLocationUpdate(String userId, DriverLocationDto update) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/driver-location",
                update
        );
    }
}