package com.fooddelivery.restaurantservice.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fooddelivery.shared.enumerate.OrderStatus;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationService webSocketNotificationService;

    @Test
    void sendOrderStatusUpdateNotification_sendsMessageToUser() {
        // Arrange
        String userId = "123";
        OrderStatus status = OrderStatus.PENDING;

        // Act
        webSocketNotificationService.sendOrderStatusUpdateNotification(userId, status);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(userId),
                eq("/queue/order-updates"),
                eq(status));
    }

    @Test
    void sendOrderStatusUpdateNotification_withAcceptedStatus_sendsMessage() {
        // Arrange
        String userId = "456";
        OrderStatus status = OrderStatus.ACCEPTED;

        // Act
        webSocketNotificationService.sendOrderStatusUpdateNotification(userId, status);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(userId),
                eq("/queue/order-updates"),
                eq(status));
    }

    @Test
    void sendOrderStatusUpdateNotification_withReadyStatus_sendsMessage() {
        // Arrange
        String userId = "789";
        OrderStatus status = OrderStatus.READY_FOR_PICKUP;

        // Act
        webSocketNotificationService.sendOrderStatusUpdateNotification(userId, status);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(userId),
                eq("/queue/order-updates"),
                eq(status));
    }

    @Test
    void sendOrderStatusUpdateNotification_withInTransitStatus_sendsMessage() {
        // Arrange
        String userId = "101";
        OrderStatus status = OrderStatus.IN_TRANSIT;

        // Act
        webSocketNotificationService.sendOrderStatusUpdateNotification(userId, status);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(userId),
                eq("/queue/order-updates"),
                eq(status));
    }

    @Test
    void sendOrderStatusUpdateNotification_withDeliveredStatus_sendsMessage() {
        // Arrange
        String userId = "202";
        OrderStatus status = OrderStatus.DELIVERED;

        // Act
        webSocketNotificationService.sendOrderStatusUpdateNotification(userId, status);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(userId),
                eq("/queue/order-updates"),
                eq(status));
    }
}
