package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderAcceptedEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderAcceptedEventPublisher {
    public static final String TOPIC = "order_accepted";
    private final KafkaTemplate<String, OrderAcceptedEvent> kafkaTemplate;

    public void publish(OrderAcceptedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}