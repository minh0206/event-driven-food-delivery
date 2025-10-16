package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.event.OrderStatusUpdateEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderStatusUpdateEventListener {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "order_status_updates", groupId = "order-service-group")
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        log.info("Updating status for order #{} to {}", event.orderId(), event.newStatus());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        order.setStatus(OrderStatus.valueOf(event.newStatus()));
        orderRepository.save(order);

        // TODO: In the future, this is where you would also push a WebSocket notification
        // to the customer about their order being accepted.
    }
}
