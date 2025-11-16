package com.fooddelivery.shared.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderDeliveredEvent;
import com.fooddelivery.shared.event.OrderInTransitEvent;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.event.OrderReadyEvent;
import com.fooddelivery.shared.event.OrderRejectedEvent;
import com.fooddelivery.shared.exception.GlobalExceptionHandler;
import com.fooddelivery.shared.publisher.DriverAssignedEventPublisher;
import com.fooddelivery.shared.publisher.DriverLocationUpdateEventPublisher;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;
import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;
import com.fooddelivery.shared.publisher.OrderRejectedEventPublisher;

@Configuration
@ConditionalOnWebApplication
public class SharedModuleAutoConfiguration {
    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    GlobalCorsConfig globalCorsConfig() {
        return new GlobalCorsConfig();
    }

    @Bean
    OrderPlacedEventPublisher orderPlacedEventPublisher(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        return new OrderPlacedEventPublisher(kafkaTemplate);
    }

    @Bean
    DriverLocationUpdateEventPublisher driverLocationUpdateEventPublisher(
            KafkaTemplate<String, DriverLocationUpdateEvent> kafkaTemplate) {
        return new DriverLocationUpdateEventPublisher(kafkaTemplate);
    }

    @Bean
    DriverAssignedEventPublisher driverAssignedEventPublisher(
            KafkaTemplate<String, DriverAssignedEvent> kafkaTemplate) {
        return new DriverAssignedEventPublisher(kafkaTemplate);
    }

    @Bean
    OrderAcceptedEventPublisher orderAcceptedEventPublisher(KafkaTemplate<String, OrderAcceptedEvent> kafkaTemplate) {
        return new OrderAcceptedEventPublisher(kafkaTemplate);
    }

    @Bean
    OrderRejectedEventPublisher orderRejectedEventPublisher(KafkaTemplate<String, OrderRejectedEvent> kafkaTemplate) {
        return new OrderRejectedEventPublisher(kafkaTemplate);
    }

    @Bean
    OrderReadyEventPublisher orderReadyEventPublisher(KafkaTemplate<String, OrderReadyEvent> kafkaTemplate) {
        return new OrderReadyEventPublisher(kafkaTemplate);
    }

    @Bean
    OrderInTransitEventPublisher orderInTransitEventPublisher(
            KafkaTemplate<String, OrderInTransitEvent> kafkaTemplate) {
        return new OrderInTransitEventPublisher(kafkaTemplate);
    }

    @Bean
    OrderDeliveredEventPublisher orderDeliveredEventPublisher(
            KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate) {
        return new OrderDeliveredEventPublisher(kafkaTemplate);
    }

}