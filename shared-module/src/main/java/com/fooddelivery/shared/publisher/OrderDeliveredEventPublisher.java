package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderDeliveredEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDeliveredEventPublisher {
    public static final String TOPIC = "order_delivered";
    private final KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate;

    public void publish(OrderDeliveredEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}