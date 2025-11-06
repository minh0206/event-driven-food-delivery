package com.fooddelivery.orderservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderDeliveredEvent;
import com.fooddelivery.shared.event.OrderInTransitEvent;
import com.fooddelivery.shared.event.OrderReadyEvent;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = OrderAcceptedEventPublisher.TOPIC, groupId = "order-service-group")
    public void handleOrderAcceptedEvent(OrderAcceptedEvent event) {
        handleOrderStatusUpdate(event.orderId(), OrderStatus.ACCEPTED);
    }

    @KafkaListener(topics = OrderReadyEventPublisher.TOPIC, groupId = "order-service-group")
    public void handleOrderReadyEvent(OrderReadyEvent event) {
        handleOrderStatusUpdate(event.orderId(), OrderStatus.READY_FOR_PICKUP);
    }

    @KafkaListener(topics = OrderInTransitEventPublisher.TOPIC, groupId = "order-service-group")
    public void handleOrderInTransitEvent(OrderInTransitEvent event) {
        handleOrderStatusUpdate(event.orderId(), OrderStatus.IN_TRANSIT);
    }

    @KafkaListener(topics = OrderDeliveredEventPublisher.TOPIC, groupId = "order-service-group")
    public void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
        handleOrderStatusUpdate(event.orderId(), OrderStatus.DELIVERED);
    }

    private void handleOrderStatusUpdate(Long orderId, OrderStatus newStatus) {
        log.info("Updating status for order #{} to {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        // Update the database
        order.setStatus(newStatus);
        orderRepository.save(order);

        // Push the update via WebSocket to the specific customer
        String customerId = String.valueOf(order.getCustomerId());
        webSocketNotificationService.sendOrderUpdate(customerId);
    }
}
