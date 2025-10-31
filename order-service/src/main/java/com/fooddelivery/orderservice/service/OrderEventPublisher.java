package com.fooddelivery.orderservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.shared.event.OrderPlacedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class OrderEventPublisher {
    private static final String TOPIC_ORDER_PLACED = "order_placed";
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final OrderMapper orderMapper;

    public void publishOrderPlacedEvent(Order order) {
        // Create the event payload
        OrderPlacedEvent event = orderMapper.toOrderPlacedEvent(order);
        // Send to Kafka
        kafkaTemplate.send(TOPIC_ORDER_PLACED, event);
    }
}
